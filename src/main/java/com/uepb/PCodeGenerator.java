package com.uepb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PCodeGenerator {
    private StringBuilder pCode;
    private Map<String, Integer> memoryMap;  // Mapeia variáveis para endereços de memória
    private int nextAvailableAddress;        // Próximo endereço de memória disponível

    public PCodeGenerator() {
        this.pCode = new StringBuilder();
        this.memoryMap = new HashMap<>();
        this.nextAvailableAddress = 0;  // Endereços começam em 0
    }

    public void addInstruction(String instruction) {
        pCode.append(instruction).append("\n");
    }

    public int allocateMemory(String varName) {
        if (!memoryMap.containsKey(varName)) {
            memoryMap.put(varName, nextAvailableAddress);
            nextAvailableAddress++;  // Incrementa para o próximo endereço disponível
        }
        return memoryMap.get(varName);  // Retorna o endereço alocado existente
    }

    public int getMemoryAddress(String varName) {
        return memoryMap.getOrDefault(varName, -1);  // Retorna o endereço ou -1 se não existir
    }

    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(pCode.toString());
        }
    }

    public String getPCode() {
        return pCode.toString();
    }
}
