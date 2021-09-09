package com.movie.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.entities.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer>{
	
	

}