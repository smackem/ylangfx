package net.smackem.ylang.lang;

import java.io.BufferedReader;
import java.io.IOException;

public interface FileProvider {
    BufferedReader open(String fileName) throws IOException;
}
