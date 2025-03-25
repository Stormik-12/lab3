package org.example;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
public class Visitor {
    private String name;
    private String surname;
    private String phone;
    private boolean subscribed;

    @SerializedName("favoriteBooks")
    private List<Book> books;
}
