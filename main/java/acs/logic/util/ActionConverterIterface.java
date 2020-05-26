package acs.logic.util;

import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;

public interface ActionConverterIterface {
	public ActionBoundary toBoundary(ActionEntity actionEntity);
	public ActionEntity toEntity(ActionBoundary actionBoundary);
	public void validateActionBoundary(Object o);
	public void validateActionEntity(Object o);
}
