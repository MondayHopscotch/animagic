#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec3 light0;
uniform vec3 light1;
uniform vec3 light2;
uniform vec3 light3;
uniform vec3 light4;
uniform vec3 light5;
uniform vec3 light6;
uniform vec3 light7;
uniform vec3 light8;
uniform vec3 light9;
uniform vec3 lightColor0;
uniform vec3 lightColor1;
uniform vec3 lightColor2;
uniform vec3 lightColor3;
uniform vec3 lightColor4;
uniform vec3 lightColor5;
uniform vec3 lightColor6;
uniform vec3 lightColor7;
uniform vec3 lightColor8;
uniform vec3 lightColor9;
uniform float attenuation0;
uniform float attenuation1;
uniform float attenuation2;
uniform float attenuation3;
uniform float attenuation4;
uniform float attenuation5;
uniform float attenuation6;
uniform float attenuation7;
uniform float attenuation8;
uniform float attenuation9;
uniform float ambientIntensity;
uniform vec3 ambientColor;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;

void main() {
    //sample color & normals from our textures
    vec4 color = texture2D(u_texture, v_texCoords.st);
    vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;

    //some bump map programs will need the Y value flipped..
    nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;

    //this is for debugging purposes, allowing us to lower the intensity of our bump map
    vec3 nBase = vec3(1.0, 1.0, 1.0);
    nColor = mix(nBase, nColor, strength);

    //normals need to be converted to [-1.0, 1.0] range and normalized
    vec3 normal = normalize(nColor * 2.0 - 1.0);

    vec4 ambient = vec4(ambientColor * ambientIntensity, 0.0);

    // ///////////////////////////////////////////////////
    vec3 deltaPos = vec3( (light0.xy - gl_FragCoord.xy) / resolution.xy, light0.z );

    vec3 lightDir = normalize(deltaPos);
    float lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    float d = sqrt(dot(deltaPos, deltaPos));
    float att = useShadow ? 1.0 / ( attenuation0 + (attenuation0*d) + (attenuation0*d*d) ) : 1.0;

    vec3 result = (lightColor0.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultA = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light1.xy - gl_FragCoord.xy) / resolution.xy, light1.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation1 + (attenuation1*d) + (attenuation1*d*d) ) : 1.0;

    result = (lightColor1.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultB = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light2.xy - gl_FragCoord.xy) / resolution.xy, light2.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation2 + (attenuation2*d) + (attenuation2*d*d) ) : 1.0;

    result = (lightColor2.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultC = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light3.xy - gl_FragCoord.xy) / resolution.xy, light3.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation3 + (attenuation3*d) + (attenuation3*d*d) ) : 1.0;

    result = (lightColor3.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultD = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light4.xy - gl_FragCoord.xy) / resolution.xy, light4.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation4 + (attenuation4*d) + (attenuation4*d*d) ) : 1.0;

    result = (lightColor4.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultE = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light5.xy - gl_FragCoord.xy) / resolution.xy, light5.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation5 + (attenuation5*d) + (attenuation5*d*d) ) : 1.0;

    result = (lightColor5.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultF = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light6.xy - gl_FragCoord.xy) / resolution.xy, light6.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation6 + (attenuation6*d) + (attenuation6*d*d) ) : 1.0;

    result = (lightColor6.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultG = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light7.xy - gl_FragCoord.xy) / resolution.xy, light7.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation7 + (attenuation7*d) + (attenuation7*d*d) ) : 1.0;

    result = (lightColor7.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultH = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light8.xy - gl_FragCoord.xy) / resolution.xy, light8.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation8 + (attenuation8*d) + (attenuation8*d*d) ) : 1.0;

    result = (lightColor8.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultI = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light9.xy - gl_FragCoord.xy) / resolution.xy, light9.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation9 + (attenuation9*d) + (attenuation9*d*d) ) : 1.0;

    result = (lightColor9.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultJ = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////

    gl_FragColor = ambient + resultA + resultB + resultC + resultD + resultE + resultF + resultG + resultH + resultI + resultJ;
}