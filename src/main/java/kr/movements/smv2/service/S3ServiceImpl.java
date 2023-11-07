package kr.movements.smv2.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import kr.movements.smv2.config.AWSConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("s3Service")
public class S3ServiceImpl extends FileUtils implements S3Service {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final String root;
    private final AWSConfiguration configuration;

    public S3ServiceImpl(AWSConfiguration awsConfiguration) {
        this.configuration = awsConfiguration;
        this.root = Paths.get("").toAbsolutePath().toString() + File.separator + "webapps" + File.separator + "data" + File.separator;
    }

    protected AWSCredentials AwsCredentials() {
        return new BasicAWSCredentials(this.configuration.getAccessKey(), this.configuration.getSecretKey());
    }

    private AmazonS3 AwsS3Client() {
        return (AmazonS3)((AmazonS3ClientBuilder)((AmazonS3ClientBuilder)AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.AwsCredentials()))).withRegion(Regions.AP_NORTHEAST_2)).build();
    }

    public String put(MultipartFile file) throws IOException {
        return this.put((String)null, (String)null, (MultipartFile)file);
    }

    public String put(String name, MultipartFile file) throws IOException {
        return this.put((String)null, name, (MultipartFile)file);
    }

    public String put(String path, String name, MultipartFile file) throws IOException {
        String fileName = name != null && !name.equals("") ? name : file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength((long)file.getBytes().length);
        this.AwsS3Client().putObject((new PutObjectRequest(this.configuration.getBucket(), this.configuration.getPath() + (path != null ? File.separator + path : "") + File.separator + fileName, file.getInputStream(), metadata)).withCannedAcl(CannedAccessControlList.PublicRead));
        return this.AwsS3Client().getUrl(this.configuration.getBucket(), fileName).toString();
    }

    public String put(String path, String name, byte[] bytes) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        InputStream is = new ByteArrayInputStream(bytes);
        metadata.setContentLength((long)bytes.length);
        this.AwsS3Client().putObject((new PutObjectRequest(this.configuration.getBucket(), this.configuration.getPath() + (path != null ? File.separator + path : "") + File.separator + name, is, metadata)).withCannedAcl(CannedAccessControlList.PublicRead));
        return this.AwsS3Client().getUrl(this.configuration.getBucket(), name).toString();
    }

    public ArrayList<String> list() {
        return this.list((String)null);
    }

    public ArrayList<String> list(String path) {
        ObjectListing ol = this.AwsS3Client().listObjects(this.configuration.getBucket(), this.configuration.getPath() + (path != null ? File.separator + path : ""));
        List<String> results = new ArrayList();
        Iterator var5 = ol.getObjectSummaries().iterator();

        while(var5.hasNext()) {
            S3ObjectSummary el = (S3ObjectSummary)var5.next();
            results.add(el.getKey());
        }

        return (ArrayList)results;
    }

    public void delete(String name) {
        this.delete((String)null, name);
    }

    public void delete(String path, String name) {
        this.AwsS3Client().deleteObject(this.configuration.getBucket(), this.configuration.getPath() + (path != null ? File.separator + path : "") + File.separator + name);
    }

    public byte[] get(String name) throws IOException {
        return this.get((String)null, name);
    }

    public byte[] get(String path, String name) throws IOException {
        S3Object o = this.AwsS3Client().getObject(this.configuration.getBucket(), this.configuration.getPath() + (path != null ? File.separator + path : "") + File.separator + name);
        S3ObjectInputStream s3is = o.getObjectContent();
        int destPos = 0;
        Long fileSize = o.getObjectMetadata().getContentLength();
        byte[] completeFile = new byte[fileSize.intValue()];

        int readLen = 0;
        for(byte[] readBuffer = new byte[2048]; (readLen = s3is.read(readBuffer)) > 0; destPos += readLen) {
            System.arraycopy(readBuffer, 0, completeFile, destPos, readLen);
        }

        return completeFile;
    }
}