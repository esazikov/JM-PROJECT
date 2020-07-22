package jm.stockx.dto;

import jm.stockx.entity.News;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewsPutDto {

    private Long id;
    private String name;
    private LocalDateTime time;
    private String title;
    private String description;
    private String text;

    public NewsPutDto(News news) {
        this.id = news.getId();
        this.name = news.getName();
        this.time = news.getTime();
        this.title = news.getTitle();
        this.description = news.getDescription();
    }

}
