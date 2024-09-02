package ch.puzzle.eft.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;

public class MockServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream outputStream;

    public MockServletOutputStream() {
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) {
        outputStream
                .write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        outputStream
                .write(b, off, len);
    }

    public byte[] getContentAsByteArray() {
        return outputStream
                .toByteArray();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }
}
