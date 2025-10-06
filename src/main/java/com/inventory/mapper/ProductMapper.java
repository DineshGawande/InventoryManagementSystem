package com.inventory.mapper;

import com.inventory.dto.ProductCreateDTO;
import com.inventory.dto.ProductDTO;
import com.inventory.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for Product entity and DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    /**
     * Convert Product entity to ProductDTO
     */
    @Mapping(target = "isLowStock", expression = "java(product.isLowStock())")
    ProductDTO toDTO(Product product);

    /**
     * Convert ProductCreateDTO to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(ProductCreateDTO createDTO);
}
