package acs.dal;


import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.ActionEntity;
import acs.data.details.ActionEntityId;

public interface ActionDao extends PagingAndSortingRepository<ActionEntity, ActionEntityId> {

}
