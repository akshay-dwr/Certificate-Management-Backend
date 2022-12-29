package com.certificate.learning.digitalCertificate.bean;


import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.cert.Certificate;

@Component
@Entity
public class Certificates {


    @Id
    private int id;
    private String aliasname;
    private String caflag;
    private Certificate certificatetest;
    private String mail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAliasname() {
        return aliasname;
    }

    public void setAliasname(String aliasname) {
        this.aliasname = aliasname;
    }

    public String getCaflag() {
        return caflag;
    }

    public void setCaflag(String caflag) {
        this.caflag = caflag;
    }

    public Certificate getCertificatetest() {
        return certificatetest;
    }

    public void setCertificatetest(Certificate certificatetest) {
        this.certificatetest = certificatetest;
    }


}