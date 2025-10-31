package poker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestMessage {
    private String name;

    @Override
    public String toString() {
        return "HelloMessage{name='" + name + "'}";
    }
}
