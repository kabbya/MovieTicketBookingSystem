package com.movie.controller;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.movie.dao.MovieticketRepository;
import com.movie.dao.UserRepository;
import com.movie.entities.Movieticket;
import com.movie.entities.User;
import com.movie.helper.Message;


@Controller
@RequestMapping("/admin")

public class AdminController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MovieticketRepository movieticketRepository;
	 
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("username "+ userName);
		
		/*get the user using userName(email)*/
		
		User user = userRepository.getUserByUserName(userName);
		
		System.out.println("User : "+user);
		
		model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","Admin Dashboard");
		return "adminuser/admin_dashboard";
	
	}
	
	@GetMapping("/upload-movie")
	public String openAddMovieForm(Model model) {
		model.addAttribute("title","Upload Movie");
		model.addAttribute("movieticket", new Movieticket () );
		return "adminuser/upload_movie_form";
	}
	
	@PostMapping("/process-movie")
	public 	String processMovie(
			@ModelAttribute Movieticket movieticket, 
			@RequestParam("profileImage") MultipartFile file,
			HttpSession session) {
		
		try {
				
			movieticket.setTotalSeat(movieticket.getSeatRemaining());
			System.out.println("Data " + movieticket);
			
			if(file.isEmpty()) {
				movieticket.setMovieImage("movieDefault.jpg");
			}
			else {
				
				movieticket.setMovieImage(file.getOriginalFilename());
				File saveFile =  new ClassPathResource("static/img").getFile();
				Path path =   Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded.");
			}
			
			session.setAttribute("message", new Message("New Movie has been successfully uploaded", "success"));
			
			Movieticket resultMovieticket	= movieticketRepository.save(movieticket);
		}
		catch (Exception e) {
			System.out.println("Error "+ e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong, try again ! ", "danger"));
			
		}
		
		return "adminuser/upload_movie_form";
		
	}
	
	
	//per page = 5[n]
	// current page = 0 [page]

	
	@GetMapping("/show-movies/{page}")
	public String showMovies(@PathVariable("page") Integer page, Model m) {
		
		m.addAttribute("title", "Movie list Admin view");
		
		LocalDate localDate = LocalDate.now(); 
	    Date date = Date.valueOf(localDate);
	    
	    Pageable pageable =  PageRequest.of(page, 5);
	    
	    Page<Movieticket> movietickets = this.movieticketRepository.findByDateGreaterThanEqualOrderByDateAsc(date,pageable);
		
		movietickets.forEach( e->{
			System.out.println(e);
		});
		
		m.addAttribute("movietickets", movietickets);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",movietickets.getTotalPages());
		
		return "adminuser/show_movies";
	}
	
	@GetMapping("/show-previous-movies/{page}")
	public String showPreviousMovies(@PathVariable("page") Integer page, Model m) {
		
		m.addAttribute("title", "Movie list Admin view");
		
		LocalDate localDate = LocalDate.now(); 
	    Date date = Date.valueOf(localDate);
	    
	    Pageable pageable =  PageRequest.of(page, 5);
	    
	    Page<Movieticket> movietickets = this.movieticketRepository.findByDateLessThanEqualOrderByDateAsc(date,pageable);
		
		movietickets.forEach( e->{
			System.out.println(e);
		});
		
		m.addAttribute("movietickets", movietickets);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",movietickets.getTotalPages());
		
		return "adminuser/show_previous_movies";
	}
	
	// delating movie
	
	@GetMapping("/delete/{id}")
	public String deleteMovie(@PathVariable("id") Integer id, Model model, HttpSession session) {
		
		Optional <Movieticket> movieTiketOptional = this.movieticketRepository.findById(id);
		Movieticket movieticket = movieTiketOptional.get();
		System.out.println(movieticket.getTotalSeat());

		System.out.println(movieticket.getSeatRemaining());
		if(movieticket.getTotalSeat()!=movieticket.getSeatRemaining()) {
			session.setAttribute("message", new Message("Unsuccessfull attempt.", "danger"));
		}
		
		else {
			this.movieticketRepository.delete(movieticket);
			session.setAttribute("message", new Message("Movie Deleted Successfully.", "success"));

		}
		
		return "redirect:/admin/show-movies/0";
		
	}
	
	// uploading movie
	
	@GetMapping("/update-movie/{id}")
	public String updateForm(@PathVariable("id") Integer id,Model m) {
		
		m.addAttribute("Title","Update Movie");
		Optional <Movieticket> movieTiketOptional = this.movieticketRepository.findById(id);
		Movieticket movieticket = movieTiketOptional.get();
		m.addAttribute("movieticket", movieticket);
		return "adminuser/update_form";
	}
	
	
	// update movie
	
	@PostMapping("/process-movie-update")
	public String movieUpdateProcess(
			@ModelAttribute Movieticket movieticket,
			@RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session) {
		
		Optional <Movieticket> movieTiketOptional = this.movieticketRepository.findById(movieticket.getId());
		Movieticket oldMovieTicket = movieTiketOptional.get();
		
		try {
			if(!file.isEmpty()) {
				
				//delete
				File deleteFile =  new ClassPathResource("static/img").getFile();
				File file1= new File(deleteFile, oldMovieTicket.getMovieImage());
				file1.delete();
				
				
				//update
				File saveFile =  new ClassPathResource("static/img").getFile();
				Path path =   Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
				movieticket.setMovieImage(file.getOriginalFilename());
			}
			
			else {
				movieticket.setMovieImage(oldMovieTicket.getMovieImage());
			}
			
			this.movieticketRepository.save(movieticket);		
			session.setAttribute("message", new Message("Movie Updated Successfully.", "success"));

			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Update :"+ movieticket);
		return "adminuser/update_form";
	}
}
