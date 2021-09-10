package com.movie.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import com.movie.entities.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer>{
	

	
	/*@Query("SELECT u FROM User u JOIN u.movieticket c WHERE u.id LIKE %?1%")
	public Page<Purchase> findAllByUser(int keyword,Pageable pageable);
	*/
	/*
	 * SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperName FROM
	 * ((Orders INNER JOIN Customers ON Orders.CustomerID = Customers.CustomerID)
	 * INNER JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID);
	 */
	
	/*
	 * @Query(
	 * value="SELECT Purchase.id, Movieticket.id, User.id, Purchase.quantity, Purchase.paymentStatus  FROM"
	 * ,nativeQuery = true) public List<Purchase> getPurches;
	 */
}