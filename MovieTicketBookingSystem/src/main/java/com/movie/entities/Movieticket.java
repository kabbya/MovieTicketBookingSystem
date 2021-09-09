package com.movie.entities;


import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="MOVIETICKET")

public class Movieticket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private String genre;
	private String day;
	private String startTime;
	
	private Date date;
	private Date duration;
	
	private int totalSeat;
	private int seatRemaining;
	private int ticketPrize;
	
	private String movieImage;
	
	
	public Movieticket() {
		super();
		// TODO Auto-generated constructor stub
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public String getDay() {
		return day;
	}


	public void setDay(String day) {
		this.day = day;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}
	
	


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public Date getDuration() {
		return duration;
	}


	public void setDuration(Date duration) {
		this.duration = duration;
	}

	

	public int getTotalSeat() {
		return totalSeat;
	}


	public void setTotalSeat(int totalSeat) {
		this.totalSeat = totalSeat;
	}


	public int getSeatRemaining() {
		return seatRemaining;
	}


	public void setSeatRemaining(int seatRemaining) {
		this.seatRemaining = seatRemaining;
	}


	public int getTicketPrize() {
		return ticketPrize;
	}


	public void setTicketPrize(int ticketPrize) {
		this.ticketPrize = ticketPrize;
	}


	public String getMovieImage() {
		return movieImage;
	}


	public void setMovieImage(String movieImage) {
		this.movieImage = movieImage;
	}


	@Override
	public String toString() {
		return "Movieticket [id=" + id + ", name=" + name + ", genre=" + genre + ", day=" + day + ", startTime="
				+ startTime + ", date=" + date + ", duration=" + duration + ", totalSeat=" + totalSeat
				+ ", seatRemaining=" + seatRemaining + ", ticketPrize=" + ticketPrize + ", movieImage=" + movieImage
				+ "]";
	}

	
}
