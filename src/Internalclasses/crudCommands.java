package Internalclasses;

import java.util.List;
import java.util.ArrayList;

//Command interface
interface crudCommands<T> {
  List<T> execute(List<T> entities);
}

//Concrete command for adding an entity
class AddCommand<T> implements crudCommands<T>{
	private T entity;
	public AddCommand(T entity) {
		this.entity = entity;
	}
	@Override
	public List<T> execute(List<T> entities) {
		List<T> updatedEntities = new ArrayList<>(entities);
		updatedEntities.add(entity);
		return updatedEntities;
	}
}

//Concrete command for updating an entity
class UpdatedCommand<T> implements crudCommands<T>{
	private T entity;
	private T updatedEntity;
	public UpdatedCommand(T entity,T updatedEntity) {
		this.entity = entity;
		this.updatedEntity = updatedEntity;
	}
	@Override
	public List<T> execute(List<T> entities) {
		List<T> updatedEntities = new ArrayList<>(entities);
		int index = updatedEntities.indexOf(entity);
		if(index!=-1) {
			updatedEntities.set(index, updatedEntity);
		}
		return updatedEntities;
	}
}

// Concrete command for deleting an entity
class DeleteCommand<T> implements crudCommands<T>{
	private T entity;
	public DeleteCommand(T entity) {
		this.entity = entity;
	}
	@Override
	public List<T> execute(List<T> entities) {
		// TODO Auto-generated method stub
		List<T> updatedEntities = new ArrayList<>(entities);
		updatedEntities.remove(entity);
		return updatedEntities;
	}	
}