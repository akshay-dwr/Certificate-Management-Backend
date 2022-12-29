package com.certificate.learning.digitalCertificate.restController;

import com.certificate.learning.digitalCertificate.bean.RenewForm;
import com.certificate.learning.digitalCertificate.bean.UserForm;
import com.certificate.learning.digitalCertificate.exception.CertificateNotFoundException;
import com.certificate.learning.digitalCertificate.repository.CertificatesRepository;
import com.certificate.learning.digitalCertificate.service.CertificateService;import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
public class CertificateRestController {
    @Autowired
    private CertificateService certificateService;
    private CertificatesRepository certificatesRepository;

    @PostMapping("/ss")
    public ResponseEntity<String> ssCertificate(@RequestBody UserForm userForm) throws Exception {
        certificateService.generateSelfSignedCertificate(userForm);
        return new ResponseEntity<>("Self Signed Certificate is created", HttpStatus.OK);
    }


    @PostMapping("/ca")
    public ResponseEntity<String> caSignedCertGeneration(@RequestBody UserForm userForm) throws Exception {
        certificateService.generateCaSignedCertificate(userForm);
        return new ResponseEntity<>("CA Signed Certificate is created", HttpStatus.OK);
    }

    @PostMapping("/signed")
    public ResponseEntity<String> SignedCertGeneration(@RequestBody UserForm userForm) throws Exception {
        certificateService.generateSignedCertificate(userForm);
        return new ResponseEntity<>("Signed Certificate is created", HttpStatus.OK);
    }

    @PutMapping("/renew")
    public ResponseEntity<String> CertificateRenewal(@RequestBody RenewForm userForm) throws Exception {
        try {
            String res =certificateService.renewCertificate(userForm);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (CertificateNotFoundException e) {
            return new ResponseEntity<>("Controller: "+e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/validate/{alias}")
    public ResponseEntity<String> validateCertificateById(@PathVariable("alias") String alias) throws Exception {
        String validity=certificateService.validateCertificate(alias);
        if(validity.equals("valid"))
            return new ResponseEntity<>("Your Certificate is VALID...It is signed by our localCA authority!!", HttpStatus.OK);
        else if (validity.equals("Invalid"))
            return new ResponseEntity<>("Your Certificate is INVALID...It is not signed by our localCA authority!!", HttpStatus.OK);

        return new ResponseEntity<>("Your Certificate is INVALID...It is a self-signed certificate!!", HttpStatus.OK);
    }



}
