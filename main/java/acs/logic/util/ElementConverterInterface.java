package acs.logic.util;

import acs.boundaries.ElementBoundary;
import acs.data.ElementEntity;

public interface ElementConverterInterface {
	public ElementBoundary toBoundary(ElementEntity elementEntity);
	public ElementEntity toEntity(ElementBoundary elementBoundary);
	public void validateElementBoundary(Object o);
	public void validateElementEntity(Object o);
}
