package com.meerkat.mytidbapp.repo;

import com.meerkat.mytidbapp.entity.MyProject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class MyProjectRepoTest {

    @Autowired
    private MyProjectRepo myProjectRepo;

    @Test
    void testCreateData() {
        for (int i = 3; i <= 100; i++) {
            MyProject myProject = new MyProject();
            myProject.setProjectNo("test" + i);
            myProject.setProjectName("test" + i);
            myProject.setProjectEndDate(LocalDate.now());
            myProjectRepo.save(myProject);
        }
    }

}