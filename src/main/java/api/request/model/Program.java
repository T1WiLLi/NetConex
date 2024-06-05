package api.request.model;

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
public class Program {
    private Long id;
    private String name;
    private Department department;
    private Long sessionId;
    private AcademicYear academicYear;
}
