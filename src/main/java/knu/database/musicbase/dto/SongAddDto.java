package knu.database.musicbase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SongAddDto{
    private String title; // 노래 title
    private String playLink; // 노래 play link
    private Integer length;     // 초 단위
    private String createAt;    // ISO 8601 String
    private long providerId; // 제공원 ID

}