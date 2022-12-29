package com.certificate.learning.digitalCertificate.service;

import com.certificate.learning.digitalCertificate.bean.RenewForm;
import com.certificate.learning.digitalCertificate.certManagement.*;
import com.certificate.learning.digitalCertificate.exception.CertificateNotFoundException;
import com.certificate.learning.digitalCertificate.bean.Certificates;
import com.certificate.learning.digitalCertificate.bean.UserForm;
import com.certificate.learning.digitalCertificate.repository.CertificatesRepository;
import com.certificate.learning.digitalCertificate.util.EmailUtil;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.spring.annotations.Recurring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Service
public class CertificateServiceImpl implements CertificateService{

    @Autowired
    private CertificatesRepository certificatesRepository;

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public void generateSelfSignedCertificate(UserForm userForm) throws Exception {
        SelfSignedCertificateGenerator c = new SelfSignedCertificateGenerator();
        String CERTIFICATE_DN = "CN="+ userForm.getCn()+", O="+ userForm.getOrganization()+", L="+ userForm.getLocality()+", ST="+ userForm.getState()+", C= "+ userForm.getCountry()+", E="+ userForm.getEmail();
        X509Certificate cer= c.createCertificate(userForm.getAlias(),CERTIFICATE_DN);
        Certificates s = c.saveFile(cer,"src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer");
        s.setMail(userForm.getEmail());
        certificatesRepository.save(s);
        emailUtil.sendEmailWithAttachment(userForm.getEmail(),
                "Self Signed CERTIFICATE",
                "Dear User, \nHere is your certificate \nIt is ready for installation to use....\n\n\nTHANK YOU",
                "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer") ;
        System.out.println("saved");

    }

    @Override
    public void generateCaSignedCertificate(UserForm userForm) throws Exception {
        CaSignedCertificateGenerator c = new CaSignedCertificateGenerator();
        String CERTIFICATE_DN = "CN="+ userForm.getCn()+", O="+ userForm.getOrganization()+", L="+ userForm.getLocality()+", ST="+ userForm.getState()+", C= "+ userForm.getCountry()+", E="+ userForm.getEmail();
        X509Certificate cert =   c.createCertificate(userForm.getAlias(),CERTIFICATE_DN);
        System.out.println("saved" );
        Certificates s =c.saveFile(cert,"src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer");
        s.setMail(userForm.getEmail());
        certificatesRepository.save(s);
        emailUtil.sendEmailWithAttachment(userForm.getEmail(),
                "Ca CERTIFICATE",
                "Dear User, \nHere is your certificate \nIt is ready for installation to use....\n\n\nTHANK YOU",
                "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer") ;
    }

    @Override
    public void generateSignedCertificate(UserForm userForm) {
        FileInputStream is = null;
        try {
            File file = new File("");
            is = new FileInputStream("src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/caLocal.test");
            KeyStore keystore = KeyStore.getInstance("JKS");
            String password = "YOUR_PASSWORD";
            keystore.load(is, password.toCharArray());
            String al = keystore.aliases().nextElement();
            Certificate certificate = keystore.getCertificate(al);

            PrivateKey key = (PrivateKey) keystore.getKey("caLocal",password.toCharArray());
            SignedCertificateGenerator c = new SignedCertificateGenerator();
            System.out.println(certificate);
            String CERTIFICATE_DN = "CN="+ userForm.getCn()+", O="+ userForm.getOrganization()+", L="+ userForm.getLocality()+", ST="+ userForm.getState()+", C= "+ userForm.getCountry()+", E="+ userForm.getEmail();
            X509Certificate certi =   c.createSignedCertificate((X509Certificate) certificate,key,CERTIFICATE_DN,userForm.getAlias());
            Certificates s= c.saveFile(certi,"src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer");
            s.setMail(userForm.getEmail());
            certificatesRepository.save(s);
            emailUtil.sendEmailWithAttachment(userForm.getEmail(),
                    "Signed CERTIFICATE",
                    "Dear User, \nHere is your certificate \nIt is ready for installation to use....\n\n\nTHANK YOU",
                    "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/"+userForm.getAlias()+".cer") ;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }}}
    }
// instead of @Scheduled(cron="0 0 12 * * ?")  you can try
//    @Recurring(id = "my-recurring-job", cron = "0 0 12 * * ?")
//    @Job(name = "My recurring job")
//	Fire at 12:00 PM (noon) every day
// for every minute-> * * * * * ?

    //this is working
    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    public void notifyExpiry() {
        List<Certificates> certificates = (List<Certificates>) certificatesRepository.findAll();
        for (int i = 0; i < certificates.size(); i++) {
            Certificates certificate = certificates.get(i);
            System.out.println("Mail reciever: "+certificate.getMail());
            X509Certificate c = (X509Certificate) certificates.get(i).getCertificatetest();
            Date d = new Date(System.currentTimeMillis());

            long diff = ((c.getNotAfter().getTime() - d.getTime()) / (1000 * 60 * 60 * 24)) %365;

            System.out.println("difference between today and expiry date is: " + diff);
            if (diff < 0) {
                System.out.println("certificate: "+ certificate.getAliasname() +" expired");
                // send mail without attachment
                emailUtil.sendEmail(certificate.getMail(), "ALERT!! CERTIFICATE EXPIRED",
                        "Dear User \\nYour certificate is expired on " + c.getNotAfter()
                                + ".\\nPlease renew your certificate....\\n\\n\\nTHANK YOU");
            } else if (diff <= 10) {
                // certificate is about to expire in diff days
                System.out.println("certificate: "+certificate.getAliasname() +" is about to expire in " + diff + " days");
                emailUtil.sendEmailWithAttachment(certificate.getMail(), "ALERT!!CERTIFICATE EXPIRY",
                        "Dear User \nYour certificate is about to expire in " + diff
                                + " days! \nPlease renew your certificate....\n\n\nTHANK YOU",
                        "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer\\"+certificate.getAliasname()+".cer");
            } else {
                System.out.println("No need to send the mail, there is a lot of time for expiration date");
            }
        }
    }

    @Override
    public String renewCertificate(RenewForm userForm) {
        String res = "";
        FileInputStream is = null;
        try {
            is = new FileInputStream("src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/" + userForm.getAlias() + ".test");
            KeyStore keystore = KeyStore.getInstance("JKS");
            String password = "YOUR_PASSWORD";
            keystore.load(is, password.toCharArray());
            PrivateKey key = (PrivateKey) keystore.getKey(userForm.getAlias(), password.toCharArray());
//            try {
//                
//            }catch(Exception e) {
//                throw new CertificatesNotFoundException("Certificate Not Found: The certificate you are tyring to renew is not found in db");
//            }
            Certificates m = certificatesRepository.getcertest(userForm.getAlias());
            X509Certificate certi = (X509Certificate) m.getCertificatetest();
            System.out.println(certi.getNotAfter());
            RenewCertificate renewedCertificate = new RenewCertificate();
            long l = ((certi.getNotAfter().getTime() - (new Date(System.currentTimeMillis()).getTime())) / ((1000 * 60 * 60 * 24)));
            System.out.println("certificate will expire in: "+l+" days");
            if (l < 0) {
                return "certificate expired, request for new one";
            } else if (l > 0 && l < 10) {
                X509Certificate c = renewedCertificate.renewCertificate(certi, key, userForm.getRenewYears(), userForm.getAlias());
                Certificates s = renewedCertificate.saveFile(c, "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/" + userForm.getAlias() + ".cer");
                System.out.println(s.getCertificatetest());
                //s.setCertificatetest(s.getCertificatetest());
                //certificatesRepository.save(s);
                certificatesRepository.updateByAlias(userForm.getAlias(), s.getCertificatetest());
                //Certificate n = certificatesRepository.getcertest(userForm.getAlias()).getCertificatetest();
                //System.out.println(n);
                emailUtil.sendEmailWithAttachment(m.getMail(),
                        "Renewed CERTIFICATE",
                        "Dear User, \nHere is your certificate \nIt is ready for installation to use....\n\n\nTHANK YOU",
                        "src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\cer/" + userForm.getAlias() + ".cer");
                return "Certificate renewed successfully";
            } else {
                return "There is still time for renewal";
            }



       } catch (Exception e) {
            throw new CertificateNotFoundException("Service: Certificate Not Found: The certificate you are tyring to renew is not found in db");
//            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new CertificateNotFoundException("Service: Certificate Not Found: The certificate you are tyring to renew is not found in db");
//                    e.printStackTrace();
                }
            }
        }
//        return res;
    }

    @Override
    public String validateCertificate(String alias) throws Exception {
        String res = "";
        FileInputStream is = new FileInputStream("src\\main\\java\\com\\certificate\\learning\\digitalCertificate\\certest/caLocal.test");
        KeyStore keystore = KeyStore.getInstance("JKS");
        String password = "YOUR_PASSWORD";
        keystore.load(is, password.toCharArray());
        String al = keystore.aliases().nextElement();
        Certificate authCert = keystore.getCertificate(al);
        X509Certificate authCertCer=(X509Certificate) authCert;
        Certificate toVerify=certificatesRepository.getcertest(alias).getCertificatetest();
        X509Certificate toVerifyCer=(X509Certificate) toVerify;
        ValidateCertificate v = new ValidateCertificate();
        Boolean result =v.verifySignature(toVerifyCer,authCertCer);
        if(result==true)
            res="valid";
        else
            res="Invalid";
        return res;
    }
}
