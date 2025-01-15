CREATE TABLE purchase(
    id int auto_increment primary key,
    nfe varchar(255),
    price DECIMAL(15,2) not null,
    created_at DATETIME not null,
    customer_id int not null,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE purchase_book (
    book_id INT NOT NULL,
    purchase_id INT NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    FOREIGN KEY (book_id) REFERENCES book(id),
    PRIMARY KEY (purchase_id, book_id)
);
