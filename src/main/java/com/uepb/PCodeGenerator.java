package com.uepb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PCodeGenerator {
    private StringBuilder pCode;

    public PCodeGenerator() {
        this.pCode = new StringBuilder();
    }

    public void addInstruction(String instruction) {
        pCode.append(instruction).append("\n");
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
