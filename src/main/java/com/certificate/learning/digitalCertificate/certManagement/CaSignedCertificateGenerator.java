package com.certificate.learning.digitalCertificate.certManagement;

import com.certificate.learning.digitalCertificate.bean.Certificates;
import com.certificate.learning.digitalCertificate.bean.UserForm;
import com.certificate.learning.digitalCertificate.repository.CertificatesRepository;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CaSignedCertificateGenerator {

    public Certificates certificates1 = new Certificates();
    //private static final String CERTIFICATE_ALIAS = "ca";
    private static final String CERTIFICATE_ALGORITHM = "RSA";
    //private static final String CERTIFICATE_DN = "CN=ca O=o, L=L, ST=il, C= c, E=cacertificate@abc.ibm.com";
    //private static final String CERTIFICATE_NAME = "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/ca.test";
    private static final int CERTIFICATE_BITS = 1024;

    static {

        // adds the Bouncy castle provider to java security
        //BouncyCastle acts similar to keytool to generate certificate
        Security.addProvider(new BouncyCastleProvider());
    }


    public X509Certificate createCertificate(String CERTIFICATE_ALIAS,String CERTIFICATE_DN) throws Exception{
        X509Certificate cert = null;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CERTIFICATE_ALGORITHM);
        //key is generated with the number of bits specified...SecureRandom() is PRNG
        keyPairGenerator.initialize(CERTIFICATE_BITS, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();


        // GENERATE THE X509 CERTIFICATE
        X509V3CertificateGenerator v3CertGen =  new X509V3CertificateGenerator();
        v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        v3CertGen.setIssuerDN(new X509Principal(CERTIFICATE_DN));
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 *365*10)));
        v3CertGen.setSubjectDN(new X509Principal(CERTIFICATE_DN));
        v3CertGen.setPublicKey(keyPair.getPublic());
        v3CertGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        //for ca cert...place in trusted dir
        v3CertGen.addExtension(X509Extensions.BasicConstraints.getId(),true,new BasicConstraints(true));
        cert = v3CertGen.generateX509Certificate(keyPair.getPrivate());
        saveCert(cert,keyPair.getPrivate(),CERTIFICATE_ALIAS);
        return cert;
    }


    public Certificates saveFile(X509Certificate cert,String Filename) throws Exception {
        final FileOutputStream os = new FileOutputStream(Filename);
        os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
        os.write(Base64.encode(cert.getEncoded()));
        os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        //certificateRepository.save(certificates1);
        os.close();
        System.out.println();
        return certificates1;
    }


    public void saveCert(X509Certificate cert, PrivateKey key,String CERTIFICATE_ALIAS) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry(CERTIFICATE_ALIAS, key, "YOUR_PASSWORD".toCharArray(),  new Certificate[]{cert});
        File file = new File("src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/"+CERTIFICATE_ALIAS+".test");
        FileOutputStream f =new FileOutputStream(file);
        keyStore.store( new FileOutputStream(file), "YOUR_PASSWORD".toCharArray());
        FileInputStream is = new FileInputStream("src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/"+CERTIFICATE_ALIAS+".test");
        KeyStore keystore = KeyStore.getInstance("JKS");
        String password = "YOUR_PASSWORD";
        keystore.load(is, password.toCharArray());
        String alias = keyStore.aliases().nextElement();
        Certificate certificate = keystore.getCertificate(alias);
        certificates1.setCaflag("T");
        certificates1.setAliasname(CERTIFICATE_ALIAS);
        certificates1.setCertificatetest(certificate);
    }}


