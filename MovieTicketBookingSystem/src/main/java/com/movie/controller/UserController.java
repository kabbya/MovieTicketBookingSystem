package com.movie.controller;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
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

import com.movie.dao.MovieticketRepository;
import com.movie.dao.PurchaseRepository;
import com.movie.dao.UserRepository;
import com.movie.entities.Movieticket;
import com.movie.entities.Purchase;
import com.movie.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MovieticketRepository movieticketRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;
	
	/*
	 * @Autowired private InformationRepository informationRepository;
	 */
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("username " + userName);

		/* get the user using userName(email) */

		User user = userRepository.getUserByUserName(userName);

		System.out.println("User : " + user);

		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		return "normaluser/user_dashboard";
	}

	@GetMapping("/show-movies/{page}")
	public String showMovies(@PathVariable("page") Integer page, Model m) {

		m.addAttribute("title", "Movie list User view");

		LocalDate localDate = LocalDate.now();
		Date date = Date.valueOf(localDate);

		Pageable pageable = PageRequest.of(page, 4);

		Page<Movieticket> movietickets = this.movieticketRepository.findByDateGreaterThanEqualOrderByDateAsc(date,
				pageable);

		movietickets.forEach(e -> {
			System.out.println(e);
		});

		m.addAttribute("movietickets", movietickets);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", movietickets.getTotalPages());

		return "normaluser/show_movies";
	}

	
	
	
	@GetMapping("/show-mywatchlist") 
	public String showMywatchlist(Model m, Principal principal) {
	  
		m.addAttribute("title", "Movie list User view");
	  
		String name =  principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		List<Purchase> purchaseList = user.getPurchase();
		
		LocalDate localDate = LocalDate.now(); 
		Date date = Date.valueOf(localDate);
		
		
	
		
		m.addAttribute("purchaseList",purchaseList);
		
		
		return "normaluser/show_mywatchlist"; 
	  
	}
	 

	@RequestMapping("/buy-ticket/{id}")
	public String buyTicket(@PathVariable("id") Integer id, Model model) {

		System.out.println("Movie Id " + id);
		Optional<Movieticket> movieticketOptional = this.movieticketRepository.findById(id);
		Movieticket movieticket = movieticketOptional.get();

		model.addAttribute("title", "Buy Movie");
		model.addAttribute("movieticket", movieticket);

		return "normaluser/buy_ticket";
	}

	@PostMapping("/process-buy-ticket/{id}")
	public String processBuyTicket(@ModelAttribute Purchase purchase, @PathVariable("id") Integer id,
			Principal principal, Model model) {

		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			Movieticket movieticket = this.movieticketRepository.getById(id);
			
			if(purchase.getQuantity() <= 0) {
				throw new Exception("Invalid Input.");
				
			}
			
			if (movieticket.getSeatRemaining() - purchase.getQuantity() < 0) {
				if (movieticket.getSeatRemaining() == 0)
					throw new Exception("No Seats are Remaining.");
				else
					throw new Exception("You can buy at most " + movieticket.getSeatRemaining() + " Tickets. ");
			}

			purchase.setUser(user);
			purchase.setMovieticket(movieticket);

			List<Purchase> purchaselist = user.getPurchase();
			purchaselist.add(purchase);

			user.setPurchase(user.getPurchase());
			userRepository.save(user);
			
			movieticket.setSeatRemaining( movieticket.getSeatRemaining() - purchase.getQuantity());
			
			System.out.println("Purchase : "+ purchase);
			
			Purchase resultPurchase = purchaseRepository.save(purchase);

		}

		catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}

		Optional<Movieticket> movieticketOptional = this.movieticketRepository.findById(id);
		Movieticket movieticket = movieticketOptional.get();

		model.addAttribute("title", "Buy Movie");
		model.addAttribute("movieticket", movieticket);

		return "normaluser/buy_ticket";
	}

	//dashboard
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("Title", "Profile Page");
		return "normaluser/profile";
	}
	
	// refund 
	
	@RequestMapping("/refund-ticket/{id}")
	public String refundTicket(@PathVariable("id") Integer id, Model model, Principal principal) {

		  System.out.println("purchase Id " + id);
		
		
		  Optional<Purchase> purchaseOptional = this.purchaseRepository.findById(id);
		  Purchase purchase = purchaseOptional.get();
		  System.out.println("purchase "+purchase);
		 	
		
		  Optional<Movieticket> movieticketOptional = this.movieticketRepository.findById( purchase.getMovieticket().getId()); 
		  Movieticket movieticket = movieticketOptional.get(); 
		  System.out.println("Movie "+movieticket);
		 
		  
		  String name =  principal.getName();
		  User user = this.userRepository.getUserByUserName(name);
			
		  model.addAttribute("title", "Refund Movie");
		  model.addAttribute("movieticket", movieticket);
		  model.addAttribute("user",user);
		  model.addAttribute("purchase",purchase);
		  
			
		  return "normaluser/refund_ticket";
	}
	
	
	
	@PostMapping("/process-refund-ticket/{id}")
	public String processRefundTicket(@ModelAttribute Purchase purchase, @PathVariable("id") Integer id,
			Principal principal, Model model) {
		
		System.out.println(purchase);
		
		String name =  principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		Optional<Purchase> purchaseOptional = this.purchaseRepository.findById(id);
		Purchase oldPurchase = purchaseOptional.get();
		
		Optional<Movieticket> movieticketOptional = this.movieticketRepository.findById( oldPurchase.getMovieticket().getId()); 
		Movieticket movieticket = movieticketOptional.get(); 
		
		System.out.println("purchase "+purchase);
		System.out.println("User "+user);
		System.out.println("Movieticket "+movieticket);
		
		try {
			if(purchase.getQuantity()<=0) {
				throw new Exception("Invalid Input.");
			}
			
			else if( oldPurchase.getQuantity() < purchase.getQuantity() ) {
				throw new Exception("Invalid Input.");
				
			}
			
			else {
				
				int refund  =  purchase.getQuantity();
				int prev = oldPurchase.getQuantity();
				
				movieticket.setSeatRemaining( movieticket.getSeatRemaining() + refund);
				user.setRefundAmount(user.getRefundAmount()+refund*movieticket.getTicketPrize());
				
				List<Purchase> purchaselist = user.getPurchase();
				
				for(int i=0; i<purchaselist.size(); i++) {
					
					if ( purchaselist.get(i).getId() == oldPurchase.getId() ) {
						
						purchaselist.remove(i);
						
					}
				}
				
				this.purchaseRepository.delete(oldPurchase);
				
				
				if(prev-refund!=0) {
					
					user.setPurchase(purchaselist);
					purchase.setUser(user);
					purchase.setMovieticket(movieticket);
					purchase.setQuantity(prev-refund);
					
					Purchase resultPurchase = purchaseRepository.save(purchase);
					purchaselist.add(resultPurchase);
					user.setPurchase(purchaselist);
					userRepository.save(user);

				}
				
				model.addAttribute("purchaseList",purchaselist);

			}
			
		}
		catch (Exception e) {
			System.out.println("Error ");
			e.printStackTrace();
		}
		
		
		return "normaluser/show_mywatchlist";
	}
	

}
