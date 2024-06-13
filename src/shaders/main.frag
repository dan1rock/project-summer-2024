#version 330 core
in vec2 TexCoord;
in vec3 FragPos;
in vec3 Normal;
in vec4 FragPosLightSpace;

out vec4 FragColor;

uniform sampler2D mainTex;
uniform sampler2D shadowMap;

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 objectColor;
uniform vec3 fogColor;

uniform float ambientStrength;
uniform float shininess;
uniform float specularStrength;
uniform float fogStart;
uniform float fogEnd;

uniform bool isTextured;

float ShadowCalculation(vec4 fragPosLightSpace) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    if (projCoords.z > 1.0f) {
        return 0.0f;
    }
    projCoords = (projCoords + 1.0) / 2.0;
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float bias = max(0.025 * (1.0 - dot(Normal, normalize(lightPos - FragPos))), 0.0005f);
    float shadow = 0.0;
    int sampleRadius = 2;
    vec2 pixelSize = 1.0 / textureSize(shadowMap, 0);
    for(int y = -sampleRadius; y <= sampleRadius; y++)
    {
        for(int x = -sampleRadius; x <= sampleRadius; x++)
        {
            float closestDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * pixelSize).r;
            if (currentDepth > closestDepth + bias)
            shadow += 1.0f;
        }
    }
    shadow /= pow((sampleRadius * 2 + 1), 2);
    return shadow;
}

void main() {
    vec4 texColor;
    if (isTextured) {
        texColor = texture(mainTex, TexCoord);
    } else {
        texColor = vec4(objectColor, 1.0);
    }

    // Ambient lighting
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse lighting
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = (dot(norm, lightDir) + 0.5) / 1.5;
    vec3 diffuse = diff * lightColor;

    // Specular lighting
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = specularStrength * spec * lightColor;

    float shadow = ShadowCalculation(FragPosLightSpace);

    // Combine all the lighting components
    vec3 lighting = (ambient + (1 - shadow) * (diffuse + specular)) * objectColor;
    vec4 result = vec4(lighting, 1.0) * texColor;

    // Calculate the fog factor
    float distance = length(viewPos - FragPos);
    float fogFactor = clamp((fogEnd - distance) / (fogEnd - fogStart), 0.0, 1.0);

    vec3 finalColor = mix(fogColor, result.rgb, fogFactor);

    FragColor = vec4(finalColor, result.a);
}