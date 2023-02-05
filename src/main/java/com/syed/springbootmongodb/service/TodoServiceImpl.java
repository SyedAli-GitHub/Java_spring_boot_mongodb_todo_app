package com.syed.springbootmongodb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.model.search.TotalSearchCount;
import com.syed.springbootmongodb.exception.TodoCollectionException;
import com.syed.springbootmongodb.model.TodoDTO;
import com.syed.springbootmongodb.repository.TodoRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class TodoServiceImpl implements TodoService {

	@Autowired
	private TodoRepository todoRepo;
	
	@Override
	public void createTodo(TodoDTO todo) throws ConstraintViolationException, TodoCollectionException {
	Optional<TodoDTO> todoOptional =	todoRepo.findByTodo(todo.getTodo());
		
	if(todoOptional.isPresent()) {
		throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
		
	}
	else {
		todo.setCreatedAt(new Date(System.currentTimeMillis()));
		todoRepo.save(todo);
	}
	}

	@Override
	public List<TodoDTO> getAllTodos() {
	List<TodoDTO> todos = todoRepo.findAll();
	if(todos.size()>0) {
		return todos;
	}
	else {
		return new ArrayList<TodoDTO>();
	}
	}

	@Override
	public TodoDTO getSingleTodo(String id) throws TodoCollectionException {
		Optional<TodoDTO> optionalTodo = todoRepo.findById(id);
		if(!optionalTodo.isPresent()) {
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
		}
		else {
			return optionalTodo.get();
		}
		
	}

	@Override
	public void updateTodo(String id, TodoDTO todo) throws TodoCollectionException {
		
		Optional<TodoDTO> todoWithId = todoRepo.findById(id);
		Optional<TodoDTO> todoWithSameName = todoRepo.findByTodo(todo.getTodo());
		
		if(todoWithId.isPresent()) {
			if(todoWithSameName.isPresent() && !todoWithSameName.get().getId().equals(id)) {
				throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
			}
			TodoDTO todoUpdate = todoWithId.get();
			todoUpdate.setTodo(todo.getTodo());
			todoUpdate.setDescription(todo.getDescription());
			todoUpdate.setCompleted(todo.isCompleted());
			todoUpdate.setUpdatedAt(new Date(System.currentTimeMillis()));
			todoRepo.save(todoUpdate);
		}
		else {
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
			
		}
		
	}

	@Override
	public void deleteTodoById(String id) throws TodoCollectionException {
		Optional<TodoDTO> todoOptional = todoRepo.findById(id);
		if(!todoOptional.isPresent()) {
			throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
			
		}
		else {
			todoRepo.deleteById(id);
		}
		
	}

	
}
