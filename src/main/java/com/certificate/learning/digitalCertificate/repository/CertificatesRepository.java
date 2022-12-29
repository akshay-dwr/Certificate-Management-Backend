package com.certificate.learning.digitalCertificate.repository;

import com.certificate.learning.digitalCertificate.bean.Certificates;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.security.cert.Certificate;
import java.sql.Blob;
import java.sql.Clob;
import java.util.List;

@Repository
public interface CertificatesRepository extends CrudRepository<Certificates,Integer> {
    @Query("select p from Certificates p where p.aliasname like :alias ")
    public Certificates getcertest(@Param("alias") String aliasname);

    @Transactional
    @Modifying
    @Query("update Certificates p set p.certificatetest =?2 where p.aliasname =?1")
    public void updateByAlias(String alias, Certificate certificatetest);

    /*@Query("select p.certificatetest from Certificates p where p.id like %:username%")
    public Certificate getcertest(@Param("username") int username);

    @Query("select p.caflag from Certificates p where p.id like %:username%")
    public String isCa(@Param("username") int username);

    @Query("select p.mail from Certificates p where p.id like %:username%")
    public String getMail(@Param("username") int username);*/


}
