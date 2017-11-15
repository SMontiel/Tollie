package com.smontiel.tollie;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

/**
 * Created by Salvador Montiel on 15/11/17.
 */
@Table("notes")
public class Note extends Model {
    @Column("title")
    public String title;
    @Column("body")
    public String body;
}
