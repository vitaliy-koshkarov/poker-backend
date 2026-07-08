package poker.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Getter
@Setter
@ToString
public class EventCard implements Serializable {
    private String rank;
    private String suit;
}
