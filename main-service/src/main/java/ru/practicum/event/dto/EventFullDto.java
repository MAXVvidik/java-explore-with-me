package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private String description;
    private String eventDate;
    private String createdOn;
    private String publishedOn;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private String title;
    private EventState state;
    private long views;
    private long confirmedRequests;
    private long participantLimit;
    public boolean requestModeration;
    private List<CommentDto> comments;
}
