use std::fs::{self, File};
use std::io::{self, Write};
use std::path::Path;
use std::time::Instant;

use strongdis_runtime::io::{csv_instance_files, load_instance};
use strongdis_runtime::strong_dis_opt::strong_dis_mm_opt;

fn main() -> io::Result<()> {
    let repo_root = Path::new(env!("CARGO_MANIFEST_DIR")).join("..");
    let files = csv_instance_files(repo_root.join("instances"))?;
    let results_dir = repo_root.join("results");
    fs::create_dir_all(&results_dir)?;

    let mut rows = Vec::new();
    for file in files {
        println!("Benchmarking {}", file.display());
        let instance = load_instance(&file)?;

        strong_dis_mm_opt(&instance.matrix, instance.n, instance.a, instance.b);

        let start = Instant::now();
        let result = strong_dis_mm_opt(&instance.matrix, instance.n, instance.a, instance.b);
        let elapsed = start.elapsed().as_nanos();

        rows.push((file.display().to_string(), elapsed, result));
    }

    let mut output = File::create(results_dir.join("rust_results_MM_opt.csv"))?;
    writeln!(output, "file,time,result")?;
    for (file, time, result) in rows {
        writeln!(output, "{file},{time},{result}")?;
    }

    Ok(())
}
