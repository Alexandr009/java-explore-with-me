package ru.practicum.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserParameters {
    List<Integer> ids;
    Integer from = 0;
    Integer size = 10;

    public void setFrom(Integer from) {
        this.from = from != null ? from : 0;
    }

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }
}
