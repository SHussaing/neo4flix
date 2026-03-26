// Neo4flix seed data (id fields are string UUID-ish for service compatibility)

// Movies
MERGE (:Movie {id:'m1', title:'The Matrix', genre:'Sci-Fi', releaseYear:1999, overview:'A hacker discovers reality is a simulation.'});
MERGE (:Movie {id:'m2', title:'Inception', genre:'Sci-Fi', releaseYear:2010, overview:'A thief steals secrets through dream-sharing technology.'});
MERGE (:Movie {id:'m3', title:'Interstellar', genre:'Sci-Fi', releaseYear:2014, overview:'Explorers travel through a wormhole in space.'});
MERGE (:Movie {id:'m4', title:'The Dark Knight', genre:'Action', releaseYear:2008, overview:'Batman faces the Joker in Gotham City.'});
MERGE (:Movie {id:'m5', title:'La La Land', genre:'Romance', releaseYear:2016, overview:'A musician and an actress fall in love in LA.'});

// Users (minimal nodes for collaborative filtering; auth service uses its own user records)
MERGE (:User {id:'u_seed_1'});
MERGE (:User {id:'u_seed_2'});
MERGE (:User {id:'u_seed_3'});

// Ratings
MATCH (u1:User {id:'u_seed_1'}), (u2:User {id:'u_seed_2'}), (u3:User {id:'u_seed_3'}),
      (m1:Movie {id:'m1'}), (m2:Movie {id:'m2'}), (m3:Movie {id:'m3'}), (m4:Movie {id:'m4'}), (m5:Movie {id:'m5'})
MERGE (u1)-[r11:RATED]->(m1) SET r11.stars=9, r11.createdAt=datetime();
MERGE (u1)-[r12:RATED]->(m2) SET r12.stars=8, r12.createdAt=datetime();
MERGE (u1)-[r13:RATED]->(m4) SET r13.stars=9, r13.createdAt=datetime();

MERGE (u2)-[r21:RATED]->(m1) SET r21.stars=8, r21.createdAt=datetime();
MERGE (u2)-[r22:RATED]->(m3) SET r22.stars=9, r22.createdAt=datetime();
MERGE (u2)-[r23:RATED]->(m4) SET r23.stars=8, r23.createdAt=datetime();

MERGE (u3)-[r31:RATED]->(m2) SET r31.stars=9, r31.createdAt=datetime();
MERGE (u3)-[r32:RATED]->(m3) SET r32.stars=8, r32.createdAt=datetime();
MERGE (u3)-[r33:RATED]->(m5) SET r33.stars=7, r33.createdAt=datetime();

