package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import acs.data.ElementEntity;
import acs.data.details.ElementEntityId;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementEntityId> {

	public List<ElementEntity> findAllByFather_ElementId(@Param("fatherId") ElementEntityId fatherId,
			Pageable pageable);

	public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByTypeLike(@Param("type") String type, Pageable pageable);

	public List<ElementEntity> findByLatBetweenAndLngBetween(@Param("latNagative") Double latNagative,
			@Param("latPositive") Double latPositive, @Param("lngNagative") Double lngNagative,
			@Param("lngPositive") Double lngPositive, Pageable pageable);

	public List<ElementEntity> findByLatBetweenAndLngBetweenAndTypeLike(@Param("latNagative") Double latNagative,
			@Param("latPositive") Double latPositive, @Param("lngNagative") Double lngNagative,
			@Param("lngPositive") Double lngPositive, @Param("type") String type, Pageable pageable);
}
