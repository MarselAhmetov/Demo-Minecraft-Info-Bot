package ru.demo_bot_minecraft.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demo_bot_minecraft.domain.dto.ServerStats;
import ru.demo_bot_minecraft.util.BinaryUtils;

@Data
@Component
@NoArgsConstructor
public class MinecraftConnector {
    private ObjectMapper mapper = new ObjectMapper();

    public ServerStats sendRequest(String address, Integer port) {
        try {
            Socket clientSocket = new Socket(address, port);

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
            inputStream.read(rawData);                // fill byte array with JSON data

            String json = new String(rawData, StandardCharsets.UTF_8);
            json = json.substring(0, json.lastIndexOf(",\"favicon\"") >= 0 ? json.lastIndexOf(",\"favicon\"") : json.length()) + "}";

            ServerStats serverStats = mapper.readValue(json, ServerStats.class);

            clientSocket.close();
            return serverStats;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
