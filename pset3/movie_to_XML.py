# David Kalish
# CSCI E 66
# pset 3

import sqlite3

# function to write the header file of all xml outfiles
def writeHeader(outfile):
    """Write the xml header to file `outfile`
    """
    outfile.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n\n")
    return


def moviesXMLWriter(outfile):
    """Write XML formatted Movies database to the `outfile` movies.xml
    """
    writeHeader(outfile)
    outfile.write("<movies>\n")
    # Create cursors and query all fields of ALL MOVIES
    cursor_1 = db.cursor()
    cursor_2 = db.cursor()
    m_results = cursor_1.execute(
        "SELECT * "
        "FROM Movie;")

    # Iterate over the rows in the movie results
    for m_row in m_results:
        m_id = m_row[0]
        m_name = m_row[1]
        m_release = m_row[2]
        m_rating = m_row[3]
        m_runtime = m_row[4]
        m_genre = m_row[5]
        m_earnings_rank = m_row[6]

        # find the directors of the movie
        d_results = cursor_2.execute(
            "SELECT Person.id "
            "FROM Movie, Person, Director "
            "WHERE Director.director_id = Person.id "
            "AND Director.movie_id = Movie.id "
            "AND Movie.id = \"{}\";".format(m_row[0]))

        # turn found director IDs into strings prepended with P
        d_ids = ""
        for d_row in d_results:
            d_ids += "P" + d_row[0] + " "


        # find actors in the movie
        a_results = cursor_2.execute("SELECT Person.id "
            "FROM Movie, Person, Actor "
            "WHERE Actor.actor_id = Person.id "
            "AND Actor.movie_id = Movie.id "
            "AND Movie.id = \"{}\"".format(str(m_id)))

        # turn found actor IDs into strings prepended with P
        a_ids = ""
        for a_row in a_results:
            a_ids += "P" + a_row[0] + " "

        # write movie ID and director IDs to movies.xml
        outfile.write("  <movie id=\"M{}\" directors=\"{}\"\n".format(m_id, d_ids.rstrip()))

        # write actor IDs to movies.xml
        outfile.write("         actors=\"{}\">\n".format(a_ids.rstrip()))

        # write the rest of the movie data to movies.xml
        outfile.write("    <name>{}</name>\n".format(m_name))
        outfile.write("    <year>{}</year>\n".format(m_release))
        if m_rating:
            outfile.write("    <rating>{}</rating>\n".format(m_rating))
        outfile.write("    <runtime>{}</runtime>\n".format(m_runtime))
        outfile.write("    <genre>{}</genre>\n".format(m_genre))
        if m_earnings_rank:
            outfile.write("    <earnings_rank>{}</earnings_rank>\n".format(m_earnings_rank))
        outfile.write("  </movie>\n")

    # Add final tags to outfiles
    outfile.write("</movies>\n")

    cursor_1.close()
    cursor_2.close()
    return


def peopleXMLWriter(outfile):
    """Write XML formatted People database to `outfile` people.xml
    """
    writeHeader(outfile)
    outfile.write("<people>\n")
    # Create cursors and query all fields of ALL PEOPLE
    cursor_1 = db.cursor()
    cursor_2 = db.cursor()
    p_results = cursor_1.execute(
        "SELECT * "
        "FROM Person;")

    # Iterate over the rows in the person results
    for p_row in p_results:
        p_id = p_row[0]
        p_name = p_row[1]
        p_dob = p_row[2]
        p_pob = p_row[3]

        # find the movies this person directed
        d_results = cursor_2.execute(
            "SELECT Movie.id "
            "FROM Movie, Person, Director "
            "WHERE Director.director_id = Person.id "
            "AND Director.movie_id = Movie.id "
            "AND Person.id = \"{}\";".format(p_id))

        # turn found movie IDs into strings prepended with M
        d_ids = ""
        for d_row in d_results:
            d_ids += "M" + d_row[0] + " "

        # find movies this person acted in
        a_results = cursor_2.execute(
            "SELECT Movie.id "
            "FROM Movie, Person, Actor "
            "WHERE Actor.actor_id = Person.id "
            "AND Actor.movie_id = Movie.id "
            "AND Person.id = \"{}\";".format(p_id))

        # turn found actor IDs into strings prepended with P
        a_ids = ""
        for a_row in a_results:
            a_ids += "M" + a_row[0] + " "

        # find oscars this person won
        o_results = cursor_2.execute(
            "SELECT Oscar.year "
            "FROM Person, Oscar "
            "WHERE Oscar.person_id = Person.id "
            "AND Person.id = \"{}\";".format(p_id))

        # turn found actor IDs into strings prepended with P
        o_ids = ""
        for o_row in o_results:
            o_year = o_row[0]
            o_id = "O{}{}".format(o_year, p_id)
            o_ids += o_id + " "

        # write person id, and movies they directed/acted in and oscars
        outfile.write("  <person id=\"P{}\"".format(p_id))
        if d_ids:
            outfile.write(" directed=\"{}\"".format(d_ids.rstrip()))
        if a_ids:
            outfile.write("\n          actedIn=\"{}\"".format(a_ids.rstrip()))
        if o_ids:
            outfile.write("\n          oscars=\"{}\"".format(o_ids.rstrip()))
        outfile.write(">\n")
        # write the rest of the person data to people.xml
        outfile.write("    <name>{}</name>\n".format(p_name))
        if p_dob:
            outfile.write("    <dob>{}</dob>\n".format(p_dob))
        if p_pob:
            outfile.write("    <pob>{}</pob>\n".format(p_pob))
        outfile.write("  </person>\n")

    # Add final tags to outfiles
    outfile.write("</people>\n")

    cursor_1.close()
    cursor_2.close()
    return


def oscarXMLWriter(outfile):
    """Write XML formatted Oscars database to `outfile` oscars.xml
    """
    writeHeader(outfile)
    outfile.write("<oscars>\n")
    # Create cursors and query all fields of ALL OSCARS
    cursor_1 = db.cursor()
    cursor_2 = db.cursor()
    o_results = cursor_1.execute(
        "SELECT * "
        "FROM Oscar;")

    # Iterate over the rows in the person results
    for o_row in o_results:
        m_id = o_row[0]
        p_id = o_row[1]
        o_type = o_row[2]
        o_year = o_row[3]

        # make an oscar ID: O<year><p_id> unless best picture, then O<year>0000000
        if o_type == "BEST-PICTURE":
            o_id = "O{}0000000".format(o_year)
        else:
            o_id = "O{}{}".format(o_year, p_id)

        # write oscar id, and associated movie/person
        outfile.write("  <oscar id=\"{}\" ".format(o_id))
        if m_id:
            outfile.write(" movie_id=\"M{}\"".format(m_id))
        if p_id:
            outfile.write(" person_id=\"P{}\"".format(p_id))
        outfile.write(">\n")
        # write the rest of the oscar data to oscars.xml
        outfile.write("    <type>{}</type>\n".format(o_type))
        outfile.write("    <year>{}</year>\n".format(o_year))
        outfile.write("  </oscar>\n")

    # Add final tags to outfiles
    outfile.write("</oscars>\n")

    cursor_1.close()
    cursor_2.close()
    return


if __name__ == "__main__":
    # Connect to the database.
    db_filename = input('name of database file: ')
    # default filename to movie.sqlite
    if db_filename == "": db_filename = "movie.sqlite"
    db = sqlite3.connect(db_filename)

    # Open the xml outfiles.
    m_outfile = open("movies.xml", 'w')
    print("movies.xml has been written")
    p_outfile = open("people.xml", 'w')
    print("people.xml has been written")
    o_outfile = open("oscars.xml", 'w')
    print("oscars.xml has been written")

    moviesXMLWriter(m_outfile)
    peopleXMLWriter(p_outfile)
    oscarXMLWriter(o_outfile)

    # Close the file and database handles.
    m_outfile.close()
    p_outfile.close()
    o_outfile.close()
    db.close()