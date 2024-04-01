package com.jspider.springmvc.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.springframework.stereotype.Component;

import com.jspider.springmvc.dto.CarDTO;
import com.jspider.springmvc.dto.UserDTO;
import com.mysql.cj.Query;

@Component
public class CarDAO {
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction entityTransaction;
	
	public void openConnection() {
		entityManagerFactory=Persistence.createEntityManagerFactory("car");
		entityManager=entityManagerFactory.createEntityManager();
		entityTransaction=entityManager.getTransaction();
	}
	
	public void closeConnection() {
		if(entityManagerFactory!=null) {
			entityManagerFactory.close();
		}
		if(entityManager!=null) {
			entityManager.close();
		}
		if(entityTransaction!=null) {
			if(entityTransaction.isActive()) {
				entityTransaction.rollback();
			}
		}
	}

	public CarDTO addCar(CarDTO carDTO) {
	
		
		openConnection();
		entityTransaction.begin();
		entityManager.persist(carDTO);
		
		entityTransaction.commit();
		closeConnection();
		return carDTO;
		
	}
	
	public List<CarDTO> findAllCars() {
		openConnection();
		javax.persistence.Query query = entityManager.createQuery("SELECT car FROM CarDTO car");
		@SuppressWarnings("unchecked")
		List<CarDTO> cars = query.getResultList();
		closeConnection();
		return cars;
	}
	
	public List<CarDTO> findAllCarsByUser(int id) {
		openConnection();
		UserDTO user = entityManager.find(UserDTO.class, id);
		List<CarDTO> cars = user.getCars();
		closeConnection();
		return cars;
	}
	public void deleteCar(int userId, int carId) {
		openConnection();
		UserDTO user = entityManager.find(UserDTO.class, userId);
		List<CarDTO> cars = user.getCars();
		CarDTO carToBeDeleted = null;
		for (CarDTO car : cars) {
			if (car.getId() == carId) {
				carToBeDeleted = car;
				break;
			}
		}
		cars.remove(carToBeDeleted);
		user.setCars(cars);
		entityTransaction.begin();
		entityManager.persist(user);
		entityTransaction.commit();
		CarDTO car = entityManager.find(CarDTO.class, carId);
		entityTransaction.begin();
		entityManager.remove(car);
		entityTransaction.commit();
		closeConnection();
	}

	public void updateCar(CarDTO car) {
		openConnection();
		CarDTO carToBeUpdated = entityManager.find(CarDTO.class, car.getId());
		carToBeUpdated.setName(car.getName());
		carToBeUpdated.setBrand(car.getBrand());
		carToBeUpdated.setPrice(car.getPrice());
		entityTransaction.begin();
		entityManager.persist(carToBeUpdated);
		entityTransaction.commit();
		closeConnection();
	}

	public CarDTO findCarById(int id) {
		openConnection();
		CarDTO car = entityManager.find(CarDTO.class, id);
		closeConnection();
		return car;
	}
	
	public List<CarDTO> findAllCarsByPrice(double low, double high) {
		openConnection();
		javax.persistence.Query query = entityManager.createQuery("SELECT car FROM CarDTO car WHERE price BETWEEN ?1 AND ?2 ");
		query.setParameter(1, low);
		query.setParameter(2, high);
		@SuppressWarnings("unchecked")
		List<CarDTO> cars = query.getResultList();
		return cars;
	}

}
