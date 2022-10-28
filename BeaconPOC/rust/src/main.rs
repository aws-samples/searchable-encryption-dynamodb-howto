use std::fs::File;
use std::io;
//use std::io::BufReader;
use base64::encode;
use std::collections::hash_map::DefaultHasher;
use std::hash::{Hash, Hasher};
use std::io::BufRead;
use std::io::Write;
use std::path::Path;

#[derive(Default)]
struct Beacon {
    name : String,
    encrypt : bool,
    prefix : Option<char>,
    split : Option<char>,
}

impl Beacon {
    fn new(name : &str, prefix : Option<char>, split : Option<char>) -> Self {
	Self {
	    name : name.to_string(), encrypt : true, prefix, split
	}
    }
    fn new_non(name : &str) -> Self {
	Self {
	    name : name.to_string(), encrypt : false, prefix : None, split : None
	}
    }
    fn get(&self, s : &str) -> String {
	if let Some(ch) = self.prefix {
	    let (a,b) = s.split_once(ch).unwrap();
	    if let Some(ch2) = self.split {
		let mut s = String::new();
		for x in b.split(ch2) {
		    s += &format!("{}", hash(x));
		    s.push(ch2);
		}
		format!("{}{}{}{}", a, ch, ch2, s)
	    } else {
		format!("{}{}{}", a, ch, hash(b))
	    }
	}
	else {
	    format!("{}", hash(s))
	}
    }
}

#[derive(Default)]
struct Beacons {
    b : Vec<Beacon>,
}

fn read_lines<P>(filename: P) -> io::Result<io::Lines<io::BufReader<File>>>
where
    P: AsRef<Path>,
{
    let file = File::open(filename)?;
    Ok(io::BufReader::new(file).lines())
}

fn hash<T: Hash + ?Sized>(t: &T) -> u64 {
    let mut s = DefaultHasher::new();
    t.hash(&mut s);
    s.finish() % 1000
}

fn encrypt(s: &str) -> String {
    s.chars().rev().collect::<String>()
}

impl Beacons {
    fn new() -> Beacons {
        let mut b = Self::default();
	b.add_plain("PK");
	b.add("SK", Some('~'), Some('.'));
	b.add_non("Target");
	b.add_plain("Role");
	b.add_plain("PK1");
	b.add("SK1", Some('~'), Some('.'));
	b.add_plain("PK2");
	b.add_plain("PK3");
	b.add("SK3", Some('~'), Some('.'));
	b.add_plain("Name");
	b.add_plain("Title");
	b
    }    
    fn add_plain(&mut self, name : &str) {
	self.b.push(Beacon::new(name, None, None))
    }
    fn add_non(&mut self, name : &str) {
	self.b.push(Beacon::new_non(name))
    }
    fn add(&mut self, name : &str, prefix : Option<char>, split : Option<char>) {
	self.b.push(Beacon::new(name, prefix, split))
    }
    fn get(&self, s : &str) -> Option<&Beacon> {
	self.b.iter().find(|&x| x.name == s)
    }
    fn print_json_header(&self, output: &mut File, do_encrypt: bool) -> io::Result<()> {
        if do_encrypt {
            write!(output, "{}", "{\"DemoEncrypted\": [")
        } else {
            write!(output, "{}", "{\"DemoPlain\": [")
        }
    }
    fn print_json_footer(&self, output: &mut File) -> io::Result<()> {
        write!(output, "{}", "\n]}\n")
    }
    fn print_json(
	&self,
        output: &mut File,
        cols: &Vec<String>,
        vals: &Vec<String>,
        need_comma: &mut bool,
        do_encrypt: bool,
    ) -> io::Result<()> {
        if *need_comma {
            write!(output, "{}", ",")?;
        }
        write!(output, "{}", "\n")?;
        *need_comma = true;
        write!(output, "{}", "{\"PutRequest\":{\"Item\":{\n")?;
        let mut need_inner_comma = false;
        if do_encrypt {
            let pk_index = cols.iter().position(|r| r == "PK").unwrap();
            let sk_index = {
                let ska = cols.iter().position(|r| r == "SK");
                if ska.is_none() {
                    cols.iter().position(|r| r.starts_with("SK+")).unwrap()
                } else {
                    ska.unwrap()
                }
            };
            let main_key = "".to_string() + &vals[pk_index] + &"_".to_string() + &vals[sk_index];
            let mut s = DefaultHasher::new();
            main_key.hash(&mut s);
            let nhash = s.finish();
            let key_val = encode(nhash.to_be_bytes());
            write!(output, "\"MainKey\":{{\"B\":\"{}\"}}", key_val)?;
            need_inner_comma = true;
        }
        for i in 0..cols.len() {
            let inner_cols = cols[i].split('+');
            if do_encrypt {
                for x in inner_cols {
		    let b = self.get(x);
                    if need_inner_comma {
                        write!(output, "{}", ",\n")?;
                    }
                    need_inner_comma = true;
		    if let Some(b) = b {
			if b.encrypt {
			    write!(output, "\"{}\":{{\"S\":\"{}\"}}", x, encrypt(&vals[i]))?;
			    write!(
				output,
				",\n\"gZ_b_{}\":{{\"S\":\"{}\"}}",
				x,
				b.get(&vals[i]),
			    )?;
			} else {
			    write!(output, "\"{}\":{{\"S\":\"{}\"}}", x, vals[i],)?;
			}
		    } else {
			write!(output, "\"{}\":{{\"S\":\"{}\"}}", x, encrypt(&vals[i]))?;
		    }
                }
            } else {
                for x in inner_cols {
                    if need_inner_comma {
                        write!(output, "{}", ",\n")?;
                    }
                    need_inner_comma = true;
                    write!(output, "\"{}\":{{\"S\":\"{}\"}}", x, vals[i],)?;
                }
            }
        }
        write!(output, "{}", "\n}}}")?;
        Ok(())
    }

    fn trans_txt<P>(&self, infile: P, outfile: P, do_encrypt: bool) -> io::Result<()>
    where
        P: AsRef<Path>,
    {
        let lines = read_lines(infile)?;
        let mut output = File::create(outfile)?;
        let mut have_header = false;
        let mut cols = Vec::new();
        let mut need_comma = false;
        self.print_json_header(&mut output, do_encrypt)?;
        for line in lines {
            let line = line?;
            if !line.starts_with('#') {
                if have_header {
                    let vals = line.split('\t').map(|s| s.to_string()).collect();
                    self.print_json(&mut output, &cols, &vals, &mut need_comma, do_encrypt)?;
                } else {
                    have_header = true;
                    cols = line.split('\t').map(|s| s.to_string()).collect();
                }
            }
        }
        self.print_json_footer(&mut output)?;
        Ok(())
    }
}

fn main() -> io::Result<()> {
    let b = Beacons::new();
    b.trans_txt("text/employee.txt", "plain_json/employee.json", false)?;
    b.trans_txt("text/employee.txt", "encrypted_json/employee.json", true)?;
    b.trans_txt("text/ticket.txt", "plain_json/ticket.json", false)?;
    b.trans_txt("text/ticket.txt", "encrypted_json/ticket.json", true)?;
    b.trans_txt("text/project.txt", "plain_json/project.json", false)?;
    b.trans_txt("text/project.txt", "encrypted_json/project.json", true)?;
    b.trans_txt("text/emeeting.txt", "plain_json/emeeting.json", false)?;
    b.trans_txt("text/emeeting.txt", "encrypted_json/emeeting.json", true)?;
    b.trans_txt("text/timecard.txt", "plain_json/timecard.json", false)?;
    b.trans_txt("text/timecard.txt", "encrypted_json/timecard.json", true)?;
    b.trans_txt("text/reservation.txt", "plain_json/reservation.json", false)?;
    b.trans_txt("text/reservation.txt", "encrypted_json/reservation.json", true)?;
    Ok(())
}
