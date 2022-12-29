package com.certificate.learning.digitalCertificate.service;


import com.certificate.learning.digitalCertificate.bean.RenewForm;
import com.certificate.learning.digitalCertificate.bean.UserForm;

import java.security.KeyStoreException;

public interface CertificateService {
    public void generateSelfSignedCertificate(UserForm userForm) throws Exception;
    public void generateCaSignedCertificate(UserForm userForm) throws Exception;
    public void generateSignedCertificate(UserForm userForm);
    public void notifyExpiry();

    public String renewCertificate(RenewForm userForm);

    public String validateCertificate(String alias) throws Exception;

}
