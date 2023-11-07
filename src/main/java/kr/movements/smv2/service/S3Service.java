package kr.movements.smv2.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

public interface S3Service {
    String put(MultipartFile var1) throws IOException;

    String put(String var1, MultipartFile var2) throws IOException;

    String put(String var1, String var2, MultipartFile var3) throws IOException;

    String put(String var1, String var2, byte[] var3) throws IOException;

    ArrayList<String> list();

    ArrayList<String> list(String var1);

    void delete(String var1);

    void delete(String var1, String var2);

    byte[] get(String var1) throws IOException;

    byte[] get(String var1, String var2) throws IOException;
}
