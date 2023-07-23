package com.meerkat.mytidbapp.repo;

import com.meerkat.mytidbapp.entity.MyProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyProjectRepo extends JpaRepository<MyProject, String> {
}
