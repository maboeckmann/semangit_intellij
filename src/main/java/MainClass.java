import com.opencsv.CSVReader;


/*
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.hdt.*;
import org.rdfhdt.hdt.header.Header;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.apache.commons.lang3.StringEscapeUtils;
import org.rdfhdt.hdt.rdf.RDFParserCallback;
import org.rdfhdt.hdt.rdf.RDFParserFactory;
import org.rdfhdt.hdt.tools.RDF2HDT;
*/

import java.io.*;

public class MainClass {

    private static final String PREFIX_Semangit = "<http://www.sg.com/ont/>";
    private static final String TAG_Semangit = "semangit:";
    private static final String TAG_Userprefix = "ghuser_";
    private static final String TAG_Repoprefix = "ghrepo_";
    private static final String TAG_Commit = "ghcom_";
    private static final String TAG_Comment = "ghcomment_";
    private static final String TAG_Issue = "ghissue_";
    private static final String TAG_Pullrequest = "ghpr_";


    public static void parseFollowers(String path, boolean includePrefix)
    {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "followers.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/followers.ttl"), 32768);
            String[] nextLine;
            if(includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while((nextLine = reader.readNext())!= null) {
                writer.write("[ a " + TAG_Semangit + "github_follow_event;");
                writer.newLine();
                writer.write(TAG_Semangit + "github_following_since \"" + nextLine[2] + "\" ;");
                writer.write(TAG_Semangit + "github_user_or_project 0 ] " + TAG_Semangit + "github_follower " + TAG_Semangit  + TAG_Userprefix + nextLine[1] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "github_follows " + TAG_Semangit  + TAG_Userprefix + nextLine[0] + ".");
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void parseOrganizationMembers(String path, boolean includePrefix)
    {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "organization_members.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/organization_members.ttl"), 32768);
            String[] nextLine;
            if(includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while((nextLine = reader.readNext())!= null) {
                writer.write("[ a " + TAG_Semangit + "github_organization_join_event;");
                writer.newLine();
                writer.write(TAG_Semangit + "github_organization_joined_at \"" + nextLine[2] + "\" ] " + TAG_Semangit + "github_organization_joined_by " + TAG_Semangit  + TAG_Userprefix + nextLine[1] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "github_organization_is_joined " + TAG_Semangit  + TAG_Userprefix + nextLine[0] + ".");
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }



    public static void parseUsers(String path, boolean includePrefix)
    {
        try
        {
            CSVReader reader = new CSVReader(new FileReader(path + "users.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/users.ttl"), 32768);
            String[] nextLine;
            if(includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while((nextLine = reader.readNext())!= null)
            {
                for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = groovy.json.StringEscapeUtils.escapeJava(nextLine[i]);
                }
                String userURI = TAG_Semangit  + TAG_Userprefix + nextLine[0];
                writer.write(userURI + " a " + TAG_Semangit + "github_user ;");
                writer.newLine();
                if(!nextLine[1].equals("N"))
                {
                    writer.write(TAG_Semangit + "github_login " + "\"" + nextLine[1] + "\";");
                    writer.newLine();
                }
                if(!nextLine[2].equals("N"))
                {
                    writer.write(TAG_Semangit + "github_name " + "\"" + nextLine[2] + "\";");
                    writer.newLine();
                }
                if(!nextLine[3].equals("N"))
                {
                    writer.write(TAG_Semangit + "github_company " + "\"" + nextLine[3] + "\";");
                    writer.newLine();
                }
                if(!nextLine[4].equals("N"))
                {
                    writer.write(TAG_Semangit + "github_user_location " + "\"" + nextLine[4] + "\";");
                    writer.newLine();
                }
                if(!nextLine[5].equals("N"))
                {
                    writer.write(TAG_Semangit + "user_email " + "\"" + nextLine[5] + "\";");
                    writer.newLine();
                }
                writer.write(TAG_Semangit + "github_user_created_at " + "\"" + nextLine[6] + "\";");
                writer.newLine();
                writer.write(TAG_Semangit + "github_user_is_org ");
                if(nextLine[7].equals("USR"))
                {
                    writer.write("false ;");
                    writer.newLine();
                }
                else
                {
                    writer.write("true ;");
                    writer.newLine();
                }
                writer.write(TAG_Semangit + "github_user_deleted ");
                if(nextLine[8].equals("0"))
                {
                    writer.write("false ;");
                    writer.newLine();
                }
                else
                {
                    writer.write("true ;");
                    writer.newLine();
                }
                writer.write(TAG_Semangit + "github_user_fake ");
                if(nextLine[9].equals("0"))
                {
                    writer.write("false .");
                    writer.newLine();
                }
                else
                {
                    writer.write("true .");
                    writer.newLine();
                }
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }



    public static void parseCommitParents(String path, boolean includePrefix) {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "commit_parents.csv"));
            //TODO: Split file and sort?
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/commit_parents.ttl"), 32768);
            String[] nextLine;
            String[] curLine;
            if (includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            curLine = reader.readNext();
            boolean abbreviated = false;
            while ((nextLine = reader.readNext()) != null) {
                if(!abbreviated)
                {
                    writer.write(TAG_Semangit + TAG_Commit + curLine[0] + " " + TAG_Semangit + "commit_has_parent " + TAG_Semangit + TAG_Commit + curLine[1]);
                }
                else
                {
                    writer.write(TAG_Semangit + TAG_Commit + curLine[1]); //only specifying next object. subject/predicate are abbreviated
                }
                if(curLine[0].equals(nextLine[0]))
                {
                    writer.write(","); //abbreviating subject and predicate for next line
                    abbreviated = true;
                }
                else
                {
                    writer.write("."); //cannot use turtle abbreviation here
                    abbreviated = false;
                }
                writer.newLine();
                curLine = nextLine;
            }
            //handle last line of file
            if(!abbreviated)
            {
                writer.write(TAG_Semangit + TAG_Commit + curLine[0] + " " + TAG_Semangit + "commit_has_parent " + TAG_Semangit + TAG_Commit + curLine[1] + ".");
            }
            else
            {
                writer.write(TAG_Semangit + TAG_Commit + curLine[1] + "."); //only specifying next object. subject/predicate are abbreviated
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void parseCommits(String path, boolean includePrefix) {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "commits.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/commits.ttl"), 32768);
            String[] nextLine;
            if (includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while ((nextLine = reader.readNext()) != null) {
                /*for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = groovy.json.StringEscapeUtils.escapeJava(nextLine[i]);
                }*/
                String commitURI = TAG_Semangit + TAG_Commit + nextLine[0];
                writer.write(  commitURI + " a " + TAG_Semangit + "github_commit;");
                writer.newLine();
                writer.write(TAG_Semangit + "commit_sha \"" + nextLine[1] + "\";");
                writer.newLine();
                writer.write(TAG_Semangit + "commit_author " + TAG_Semangit + TAG_Userprefix + nextLine[2] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "commit_committed_by " + TAG_Semangit + TAG_Userprefix + nextLine[3] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "commit_repository " + TAG_Semangit + TAG_Repoprefix + nextLine[4] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "commit_created_at \"" + nextLine[5] + "\".");
                writer.newLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Comment section. Below are all functions related to comments.
     * commit_comments
     * issue_comments
     * pull_request_comments
     */

    public static void parseCommitComments(String path, boolean includePrefix) {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "commit_comments.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/commit_comments.ttl"), 32768);
            String[] nextLine;
            if (includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while ((nextLine = reader.readNext()) != null) {
                for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = groovy.json.StringEscapeUtils.escapeJava(nextLine[i]);
                }
                writer.write(TAG_Semangit + TAG_Comment + "commit_" + nextLine[0] + " a " + TAG_Semangit + "comment;");
                writer.newLine();
                writer.write(TAG_Semangit + "comment_for " + TAG_Semangit + TAG_Commit + nextLine[1] + ";"); //comment for a commit
                writer.newLine();
                writer.write(TAG_Semangit + "comment_author " + TAG_Semangit + TAG_Userprefix + nextLine[2] + ";");
                writer.newLine();
                if(!nextLine[3].equals("N"))
                {
                    writer.write(TAG_Semangit + "comment_body \"" + nextLine[3] + "\";");
                    writer.newLine();
                }

                if(!nextLine[4].equals("N"))
                {
                    writer.write(TAG_Semangit + "comment_line " + nextLine[4] + ";");
                    writer.newLine();
                }

                if(!nextLine[5].equals("N"))
                {
                    writer.write(TAG_Semangit + "comment_pos " + nextLine[5] + ";");
                    writer.newLine();
                }


                writer.write(TAG_Semangit + "comment_created_at \"" + nextLine[7] + "\".");
                writer.newLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void parseIssueComments(String path, boolean includePrefix) {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "issue_comments.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/issue_comments.ttl"), 32768);
            String[] nextLine;
            if (includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while ((nextLine = reader.readNext()) != null) {
                /*for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = groovy.json.StringEscapeUtils.escapeJava(nextLine[i]);
                }*/

                //TODO: Let's verify the integrity of the RDF output of this
                writer.write("[" + TAG_Semangit + "comment_created_at \"" + nextLine[3] + "\";");
                writer.newLine();
                writer.write(TAG_Semangit + "comment_for " + TAG_Semangit + TAG_Issue + nextLine[0] + ";"); //comment for an issue
                writer.newLine();
                writer.write(TAG_Semangit + "comment_author " + TAG_Semangit + TAG_Userprefix + nextLine[1] + "] a " + TAG_Semangit + "comment.");
                writer.newLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }



    public static void parsePullRequestComments(String path, boolean includePrefix) {
        try {
            CSVReader reader = new CSVReader(new FileReader(path + "pull_request_comments.csv"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "rdf/pull_request_comments.ttl"), 32768);
            String[] nextLine;
            if (includePrefix) {
                writer.write("@prefix semangit: " + PREFIX_Semangit + " .");
                writer.newLine();
                writer.newLine();
            }
            while ((nextLine = reader.readNext()) != null) {
                for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = groovy.json.StringEscapeUtils.escapeJava(nextLine[i]);
                }

                //TODO: Let's verify the integrity of the RDF output of this
                writer.write("[" + TAG_Semangit + "comment_created_at \"" + nextLine[6] + "\";");
                writer.newLine();
                writer.write(TAG_Semangit + "comment_for " + TAG_Semangit + TAG_Pullrequest + nextLine[0] + ";"); //comment for a pull request
                writer.newLine();
                writer.write(TAG_Semangit + "comment_pos " + nextLine[3] + ";");
                writer.newLine();
                writer.write(TAG_Semangit + "comment_body \"" + nextLine[4] + "\";");
                writer.newLine();
                //TODO: commit_id?!

                writer.write(TAG_Semangit + "comment_author " + TAG_Semangit + TAG_Userprefix + nextLine[1] + "] a " + TAG_Semangit + "comment.");
                writer.newLine();
                //comment for [0]
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * Files still to be converted:
     *     issue_events
     *     issue_labels
     *     issues
     *     project_commits
     *     project_members
     *     projects
     *     pull_request_commits
     *     pull_request_history
     *     pull_requests
     *     repo_labels
     *     repo_milestones
     *     watchers
     */




    //removed! HDT fails to satisfy our needs, as there are too many bugs in the software, making it impossible to merge very large files
    /*public static void rdf2hdt(String path, String table)
    {
        String baseURI = "http://example.com/mydataset";
        String rdfInput = path.concat("rdf/" + table + ".ttl");
        String inputType = "turtle";    commit_comments
    commit_parents
    commits
    issue_comments
    issue_events


        String hdtOutput = path.concat("hdt/" + table + ".hdt");
        try
        {
            //HDTSpecification specification = new HDTSpecification();
            //specification.set("triples.format", HDTVocabulary.TRIPLES_TYPE_TRIPLESLIST);
            HDT hdt = HDTManager.generateHDT(rdfInput, baseURI, RDFNotation.parse(inputType), new HDTSpecification(), null);
            hdt.saveToHDT(hdtOutput, null);
            hdt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }*/

    public static void appendFileToOutput(String directory, String fileName)
    {

        String outPath = directory.concat("combined.ttl");
        try(BufferedReader br = new BufferedReader(new FileReader(directory.concat(fileName)))) {
            Writer output = new BufferedWriter(new FileWriter(outPath, true));
            for(String line; (line = br.readLine()) != null; ) {
                output.append(line);
                ((BufferedWriter) output).newLine();
            }
            output.close();
            // line is not visible here.
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        new File(args[0] + "rdf").mkdirs();
        new File(args[0] + "hdt").mkdirs();

        parseOrganizationMembers(args[0], true);
        //rdf2hdt(args[0], "organization_members");
        System.out.println("Organizations parsed.");

        parseFollowers(args[0], false);
        System.out.println("Followers parsed.");

        parseUsers(args[0], false);
        System.out.println("Users parsed.");

        parseCommits(args[0], false);
        System.out.println("Committs parsed.");

        parseCommitParents(args[0], false);
        System.out.println("CommitParents parsed.");

        parseCommitComments(args[0], false);
        System.out.println("CommitComments parsed.");

        parseIssueComments(args[0], false);
        System.out.println("IssueComments parsed.");

        parsePullRequestComments(args[0], false);
        System.out.println("PullRequestComments parsed.");
        try {
            /*String correctPath = args[0].concat("rdf/");
            System.out.println("Appending Org Members");
            
            appendFileToOutput(correctPath, "organization_members.ttl");

            System.out.println("Appending followers");
            appendFileToOutput(correctPath, "followers.ttl");

            System.out.println("Appending users");
            appendFileToOutput(correctPath, "users.ttl");
            */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
