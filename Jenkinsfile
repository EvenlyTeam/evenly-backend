@Library('hobom-shared-lib') _
hobomPipeline(
  serviceName:    'dev-evenly-backend',
  hostPort:       '8091',
  containerPort:  '8080',
  memory:         '1536m',
  cpus:           '2',
  envPath:        '/etc/hobom-dev/dev-evenly-backend/.env',
  addHost:        true,
  smokeCheckPath: '/actuator/health'
)
