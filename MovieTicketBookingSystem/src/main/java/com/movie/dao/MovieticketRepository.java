package com.movie.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.entities.Movieticket;

public interface MovieticketRepository extends JpaRepository<Movieticket, Integer>{
	
	public List<Movieticket> findByGenre(String genre);
	
	public List<Movieticket> findByDate(Date date);
	
	
	/*
	 * public List<Movieticket> findByDateGreaterThanEqual(Date date);
	 */
	
	//currentPage-page
	//movie per page - 5
	public Page<Movieticket> findByDateGreaterThanEqualOrderByDateAsc(Date date, Pageable pePageable);
	
	public Page<Movieticket> findByDateLessThanEqualOrderByDateAsc(Date date, Pageable pePageable);
	

}
