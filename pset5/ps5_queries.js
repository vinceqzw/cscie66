/*
 * CSCI E-66: Problem Set 5, MongoDB Programming Problems
 *
 * Put your name and email address below:
 *     name:    David Kalish
 *     email:   davidpkalish@gmail.com
 */

/*********************************************************************
 * REMEMBER:
 *  1. For each problem, you should assign your MongoDB method call
 *     to the variable called "results" that we have provided.
 *     Follow the model shown in the sample query below.
 *  2. You should *not* make any other additions or modifications to
 *     this file.
 *  3. You should test that the queries in this file are correct by
 *     executing all of the queries in the file from the command line.
 *     See the assignment for more details.
 *********************************************************************/

/* Do not modify the following lines. */
db = db.getSiblingDB('imdb')
function printResults(results) {
    if (results instanceof DBQuery) {
        results.forEach(printjson)
    } else if (Array.isArray(results)) {
	printjson(results)
    } else if (!isNaN(results)) {
        print(results)
    } else {
        printjson(results.result)
    }
}

/*
 * Sample query: Find the names of all movies in the database from 1990.
 */

print()
print("results of sample query")
print("-----------------------")

results = db.movies.find( { year: 1990 },
                          { name: 1, _id: 0 } )

printResults(results)


/*
 * Query 1. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 1")
print("------------------")

results = db.movies.find( { year: 2000, rating: "PG-13" },
                          { name: 1, _id: 0 } )

printResults(results)


/*
 * Query 2. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 2")
print("------------------")

results = db.people.find( { $or: [ { name: "Emma Stone" },
                                   { name: "Viola Davis" }
                                 ]
                          },
                          { name: 1, pob: 1, dob: 1, _id: 0 } )

printResults(results)


/*
 * Query 3. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 3")
print("------------------")

results = db.oscars.find( { $and: [ { year: { $gte: 1990}},
                                    { year: { $lte: 1999}}
                                  ],
                            type: "BEST-PICTURE"
                          },
                          { "movie.name": 1, year: 1, _id: 0 } )

printResults(results)


/*
 * Query 4. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 4")
print("------------------")

results = db.people.find( {hasDirected: true, pob: /France/},
                          {name: 1, pob: 1, _id:0})

printResults(results)


/*
 * Query 5. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 5")
print("------------------")

results =

printResults(results)


/*
 * Query 6. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 6")
print("------------------")

results =

printResults(results)


/*
 * Query 7. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 7")
print("------------------")

results =

printResults(results)


/*
 * Query 8. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 8")
print("------------------")

results =

printResults(results)


/*
 * Query 9. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 9")
print("------------------")

results =

printResults(results)


/*
 * Query 10. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 10")
print("-------------------")

results =

printResults(results)


/*
 * Query 11. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 *
 * required for grad-credit students; optional for others
 */

print()
print("results of query 11")
print("-------------------")

results =

printResults(results)
