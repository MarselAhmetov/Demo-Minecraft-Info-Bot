package ru.demo_bot_minecraft.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demo_bot_minecraft.domain.dto.*;
import ru.demo_bot_minecraft.util.BinaryUtils;

@Data
@Component
@RequiredArgsConstructor
@Slf4j
public class MinecraftClient {
    private ObjectMapper mapper = new ObjectMapper();

    public ServerStatsResponse sendRequest(String address, Integer port) {
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(address, port), 5000);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
            DataOutputStream socketOutputStream =
                new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream inputStream =
                new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            outputStream.writeByte(0x00);               // handshake packet
            BinaryUtils.sendIntData(outputStream, 0x00);         // protocol version
            BinaryUtils.sendIntData(outputStream, address.length());    // packed remote address length as varint
            outputStream.writeBytes(address);              // remote address as string
            outputStream.writeShort(port);                 // remote port as short
            BinaryUtils.sendIntData(outputStream, 0x01);         // state packet

            BinaryUtils.sendIntData(socketOutputStream, byteArrayOutputStream.size()); // payload size as varint
            socketOutputStream.write(byteArrayOutputStream.toByteArray());            // send payload
            socketOutputStream.writeByte(0x01);                   // size
            socketOutputStream.writeByte(0x00);                   // ping packet

            int totalLength = BinaryUtils.getIntData(inputStream);        // total response size
            int packetID = BinaryUtils.getIntData(inputStream);           // packet ID
            int jsonLength = BinaryUtils.getIntData(inputStream);         // JSON response size
            byte[] rawData = new byte[jsonLength];    // storage for JSON data
            inputStream.readFully(rawData);                // fill byte array with JSON data

            String json = new String(rawData, StandardCharsets.UTF_8);
//            int faviconIndex = json.lastIndexOf(",\"favicon\"");
//            // TODO add response configuration
//            if (faviconIndex >= 0) {
//                json = json.substring(0, faviconIndex) + "}";
//            }

//            int forgeDataIndex = json.lastIndexOf(",\"forgeData\":");
//            if (forgeDataIndex >= 0) {
//                json = json.substring(0, forgeDataIndex) + "}";
//            }

            ServerStats serverStats = mapper.readValue(json, ServerStats.class);
            serverStats.setDescriptionText(extractPlainText(serverStats.getDescription()));
            serverStats.setFavicon(null);

            clientSocket.close();
            return ServerStatsResponse.builder()
                .serverStats(Optional.ofNullable(serverStats))
                .build();
        } catch (Exception e) {
            return ServerStatsResponse.builder()
                .error(e.getMessage())
                .build();
        }
    }

    public static String extractPlainText(Description description) {
        StringBuilder sb = new StringBuilder();

        if (description.getText() != null) {
            sb.append(description.getText());
        }

        if (description.getExtra() != null) {
            for (DescriptionPart part : description.getExtra()) {
                appendTextRecursive(sb, part);
            }
        }

        return sb.toString();
    }

    private static void appendTextRecursive(StringBuilder sb, DescriptionPart part) {
        if (part.getText() != null) {
            sb.append(part.getText());
        }

        if (part.getExtra() != null) {
            for (DescriptionPart child : part.getExtra()) {
                appendTextRecursive(sb, child);
            }
        }
    }
}
