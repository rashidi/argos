db.createUser(
    {
        user: "argos",
        pwd: "36eUE%YbLHkYxlYj",
        roles: [
            {
                role: "readWrite",
                db: "test"
            }
        ]
    }
);

db.createCollection("test");
