package api.request.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Session {
    private Long sessionId;
    private AcademicYear academicYear;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
