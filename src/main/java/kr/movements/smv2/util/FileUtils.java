//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kr.movements.smv2.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import org.mozilla.universalchardet.CharsetListener;
//import org.mozilla.universalchardet.UniversalDetector;
//import org.springframework.Boot;
import kr.movements.smv2.common.util.Boot;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service("fileUtilsService")
public class FileUtils extends org.apache.commons.io.FileUtils {
    private ConvertUtil conv = ConvertUtil.getInstance();
    private final String root;
    private String path;
    private String target;
    private File file;
    private SimpleDateFormat inSDT;
    protected static FileUtils instance = new FileUtils();

    protected FileUtils() {
        this.root = Paths.get("").toAbsolutePath().toString() + File.separator + "webapps" + File.separator + "data" + File.separator;
        this.path = null;
        this.target = null;
        this.file = null;
        this.inSDT = new SimpleDateFormat("yyyyMMdd");
    }

    public static FileUtils getInstance() {
        return instance;
    }

    private void init() {
        this.file = new File(this.root);
        if (!this.file.exists()) {
            this.file.mkdirs();
        }

    }

    public void fileMaker(int date, String name) {
        if (this.conv.isNOE(this.root)) {
            this.init();
        }

        this.target = date + File.separator + name + File.separator;
        this.path = this.root + this.target;
        this.file = new File(this.path);
        if (!this.file.exists()) {
            this.file.mkdirs();
        }

    }

    public void fileMaker(String name) {
        Calendar cal = Calendar.getInstance();
        this.fileMaker(Integer.parseInt(this.inSDT.format(cal.getTime())), name);
    }

    public File fileMake(String name) {
        return new File(this.root + name);
    }

    public void fileRemove(String name) {
        if (this.conv.isNOE(this.root)) {
            this.init();
        }

        this.file = new File(this.root + name);
        if (this.file.exists()) {
            this.file.delete();
        }

    }

    public byte[] getFile(String name) throws IOException {
        if (this.conv.isNOE(this.root)) {
            this.init();
        }

        return readFileToByteArray(new File(this.root + name));
    }
    public String getImgExtension(String contentType) {
        String result = null;
        if (!ObjectUtils.isEmpty(contentType)) {
            if (contentType.contains(Boot.jpeg.getValue())) {
                result = Boot.jpeg.getName();
            } else if (contentType.contains(Boot.png.getValue())) {
                result = Boot.png.getName();
            } else if (contentType.contains(Boot.gif.getValue())) {
                result = Boot.gif.getName();
            } else if (!contentType.contains(Boot.bmp.getValue()) && !contentType.contains(Boot.bmpx.getValue())) {
                if (contentType.contains(Boot.svg.getValue())) {
                    result = Boot.svg.getName();
                }
            } else {
                result = Boot.bmp.getName();
            }
        }

        return result;
    }

    public String getFileExtension(String contentType) {
        String result = null;
        if (!ObjectUtils.isEmpty(contentType) && contentType.contains(Boot.csv.getValue())) {
            result = Boot.csv.getName();
        }

        return result;
    }


    public boolean equals(Boot type, String contentType) {
        return type.getValue().equals(contentType);
    }
/*
    public String readToEncoding(InputStream is) throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector((CharsetListener)null);

        int nread;
        while((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }

        detector.dataEnd();
        return detector.getDetectedCharset();
    }

    public InputStream readToFile(String encoding, MultipartFile file) throws IOException {
        if (this.conv.isNOE(encoding)) {
            encoding = this.readToEncoding(file.getInputStream());
        }

        if (encoding == null) {
            encoding = "UTF-8";
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), encoding));
        StringBuilder sb = new StringBuilder();

        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\r\n");
        }

        br.close();
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
*/
    public ConvertUtil getConv() {
        return this.conv;
    }

    public String getRoot() {
        return this.root;
    }

    public String getPath() {
        return this.path;
    }

    public String getTarget() {
        return this.target;
    }

    public File getFile() {
        return this.file;
    }

    public SimpleDateFormat getInSDT() {
        return this.inSDT;
    }
}
