package qc.netconex;

import qc.netconex.model.Student;
import qc.netconex.request.Get;

public class Main {
    public static void main(String[] args) throws Exception {
        Get getRequest = new Get(new HttpRequester("https://cms-api-1crm.onrender.com/api/v1/"));
        Student student = getRequest.executeAndDeserialize("students/1", Student.class);
        System.out.println(student);
    }
}