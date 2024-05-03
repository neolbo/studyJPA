package jpa_ex.shop.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResult <T>{
    private int size;
    private T data;
}
