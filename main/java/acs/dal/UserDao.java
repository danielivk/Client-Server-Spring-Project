package acs.dal;


import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.UserEntity;
import acs.data.details.UserEntityId;

public interface UserDao extends PagingAndSortingRepository<UserEntity, UserEntityId> {

}
