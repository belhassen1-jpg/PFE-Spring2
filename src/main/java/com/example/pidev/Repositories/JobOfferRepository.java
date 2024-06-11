package com.example.pidev.Repositories;

import com.example.pidev.Entities.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long>{

    List<JobOffer> findByCategoryAndLocation(String category, String location);

    List<JobOffer> findByCategory(String category);

    List<JobOffer> findByLocation(String location);

    List<JobOffer> findAll();

}
