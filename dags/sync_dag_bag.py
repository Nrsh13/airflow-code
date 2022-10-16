from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from airflow.operators.bash_operator import BashOperator
from airflow.models import Variable
from datetime import datetime, timedelta


default_args = {
    'owner': 'admin',
    'start_date': datetime(2019, 3, 14),
    'email': ['nrsh13@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(seconds=1)
}

dag = DAG('sync_dag_bag', default_args=default_args, schedule_interval='*/2 * * * *', catchup=False)
repo_url = "https://github.com/Nrsh13/airflow-code.git"
repo_dir = repo_url.split('/')[-1].split('.')[0]
#env = Variable.get("environment", deserialize_json=True)
env = "persistent"

git_pull_command = """
    set -ex
    # setup path
    base_path=/usr/local/airflow
    repos=$base_path/repos
    cd $repos
    
    # clone repo
    if [ ! -d {{ params.repo_dir }} ]
    then
        git -c http.sslVerify=false clone {{ params.repo_url }} {{ params.repo_dir }}
        cd {{ params.repo_dir }}
        git checkout {{ params.branch }}
    else
        cd {{ params.repo_dir }}
        git -c http.sslVerify=false pull
    fi
"""

git_pull = BashOperator(
    task_id= 'git_pull',
    bash_command=git_pull_command,
    params = {
        "repo_dir": repo_dir,
        "repo_url": repo_url,
        'branch': 'master'
        },
    dag=dag
)

sync_dags_command = """
    set -ex
    # setup path
    base_path=/usr/local/airflow
    repos=$base_path/repos
    cd $repos
    # sync with dag folder
    rsync -rc --delete-before --exclude benefits {{ params.repo_dir }}/dags $base_path
    rsync -rc --delete-before {{ params.repo_dir }}/plugins $base_path
    rsync -rc --delete-before {{ params.repo_dir }}/pipelines $base_path
"""

sync_dags = BashOperator(
    task_id= 'sync_dags',
    bash_command=sync_dags_command,
    params = {
        "repo_dir": repo_dir
        },
    dag=dag
)

git_pull >> sync_dags
