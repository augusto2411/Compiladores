package com.uepb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PCodeGenerator {
    private StringBuilder pCode;
    private Map<String, Integer> memoryMap;  // Mapeia variáveis para endereços de memória
    private int nextAvailableAddress;        // Próximo endereço de memória disponível
    private Stack<Integer> freeAddresses;         // Armazena endereços liberados para reutilização

    public PCodeGenerator() {
        this.pCode = new StringBuilder();
        this.memoryMap = new HashMap<>();
        this.freeAddresses = new Stack<>();
        this.nextAvailableAddress = 0;  // Endereços começam em 0
    }

    public void addInstruction(String instruction) {
        pCode.append(instruction).append("\n");
    }

    public int allocateMemory(String varName) {
        int address;
        if (freeAddresses.isEmpty()) {
            address = nextAvailableAddress++;
        } else {
            address = freeAddresses.pop();  // Reutiliza endereço liberado
        }
        memoryMap.put(varName, address);
        return address;
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

    public void clearMemoryByRemoving(String varName) {
        Integer address = memoryMap.remove(varName);
        if (address != null) {
            freeAddresses.push(address);  // Libera o endereço para reutilização
        }
    }
}